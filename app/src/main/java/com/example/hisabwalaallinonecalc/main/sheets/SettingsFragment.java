package com.example.hisabwalaallinonecalc.main.sheets;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.hisabwalaallinonecalc.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceClickListener {

    private Preference themePref;
    private Preference languagePref;
    private Preference cleanPref;
    private SharedPreferences defaultSharedPrefs;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        defaultSharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        themePref = findPreference("theme");
        languagePref = findPreference("language");
        cleanPref = findPreference("clean");

        if (languagePref != null) {
            languagePref.setVisible(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU);
            languagePref.setOnPreferenceClickListener(this);
        }

        if (themePref != null) themePref.setOnPreferenceClickListener(this);
        if (cleanPref != null) cleanPref.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(@NonNull Preference preference) {
        if ("theme".equals(preference.getKey())) {
            showThemeDialog();
        } else if ("language".equals(preference.getKey())) {
            openLanguageSettings();
        } else if ("clean".equals(preference.getKey())) {
            clearHistory();
        }
        return true;
    }

    private void showThemeDialog() {
        int currentTheme = defaultSharedPrefs.getInt("themeSetting", 2);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.theme)
                .setCancelable(false)
                .setSingleChoiceItems(
                        getResources().getStringArray(R.array.theme_options),
                        currentTheme,
                        (dialog, which) -> defaultSharedPrefs.edit().putInt("themeSetting", which).apply()
                )
                .setPositiveButton(android.R.string.ok, (dialog, which) -> applyTheme())
                .show();
    }

    private void applyTheme() {
        int theme = defaultSharedPrefs.getInt("themeSetting", 2);
        switch (theme) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 2:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    private void openLanguageSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                Intent intent = new Intent(Settings.ACTION_APP_LOCALE_SETTINGS);
                intent.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
                startActivity(intent);
            } catch (Exception e) {
                try {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
                    startActivity(intent);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void clearHistory() {
        SharedPreferences history = requireActivity().getSharedPreferences("history", MODE_PRIVATE);
        history.edit().putString("newHistory", "").apply();
        Toast.makeText(getContext(), getString(R.string.hasCleaned), Toast.LENGTH_SHORT).show();
    }
}
