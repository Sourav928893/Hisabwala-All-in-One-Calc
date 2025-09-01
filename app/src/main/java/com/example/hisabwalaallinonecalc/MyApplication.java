package com.example.hisabwalaallinonecalc;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.window.embedding.RuleController;

import com.google.android.material.color.DynamicColors;

/**
 * Custom Application class
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Apply dynamic colors (Material You)
        DynamicColors.applyToActivitiesIfAvailable(this);

        // Load saved preferences
        SharedPreferences defaultSp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Theme setting: 0 = Light, 1 = Dark, 2 = Follow system
        int theme = defaultSp.getInt("themeSetting", 2);
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

        // Split-screen / embedding rules (if enabled in settings)
        boolean isActivityEmbeddingEnabled = defaultSp.getBoolean("split", false);
        if (isActivityEmbeddingEnabled) {
            RuleController.getInstance(this)
                    .setRules(RuleController.parseRules(this, R.xml.main_split_config));
        }

        // Debug mode setup (CrashHandler + Debug Toast)
        if ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            MyCrashHandler.init(getApplicationContext());
            Toast.makeText(this, "Debug mode enabled", Toast.LENGTH_SHORT).show();
        }
    }
}
