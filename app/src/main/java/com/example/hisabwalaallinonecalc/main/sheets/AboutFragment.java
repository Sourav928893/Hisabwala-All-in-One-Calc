package com.example.hisabwalaallinonecalc.main.sheets;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;

import com.example.hisabwalaallinonecalc.R;
import com.example.hisabwalaallinonecalc.utils.PaymentUtil;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

public class AboutFragment extends Fragment {

    private AppUpdateManager appUpdateManager;
    private Task<AppUpdateInfo> appUpdateInfoTask;

    private final ActivityResultLauncher<IntentSenderRequest> updateLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartIntentSenderForResult(),
                    result -> {
                        if (result.getResultCode() != RESULT_OK) {
                            Toast.makeText(requireContext(), getString(R.string.checkNet), Toast.LENGTH_SHORT).show();
                            Log.e("AboutFragment", "App update failed with code: " + result.getResultCode());
                        }
                    });

    private final CustomTabsIntent webIntent = new CustomTabsIntent.Builder().setShowTitle(true).build();

    public AboutFragment() {
        // Default constructor
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Continue update flow if in progress
        if (appUpdateManager != null) {
            appUpdateManager.getAppUpdateInfo()
                    .addOnSuccessListener(info -> {
                        if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                            startUpdateFlow(info);
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        appUpdateManager = null;
        appUpdateInfoTask = null;
    }

    @SuppressLint("SetTextI18n")
    private void init(View view) {
        TextView versionText = view.findViewById(R.id.about_app_version);

        // Set App Version safely
        try {
            String version;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                version = requireContext().getPackageManager()
                        .getPackageInfo(requireContext().getPackageName(),
                                PackageManager.PackageInfoFlags.of(0))
                        .versionName;
            } else {
                version = requireContext().getPackageManager()
                        .getPackageInfo(requireContext().getPackageName(), 0)
                        .versionName;
            }
            versionText.setText(getString(R.string.app_version) + " " + version);
        } catch (PackageManager.NameNotFoundException e) {
            versionText.setText(getString(R.string.app_version));
        }

        versionText.setOnLongClickListener(v -> {
            Toast.makeText(requireContext(), getString(R.string.thank), Toast.LENGTH_LONG).show();
            return true;
        });

        // Rate App


        // Share App
        view.findViewById(R.id.about_share).setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareContent));
            startActivity(Intent.createChooser(share, getString(R.string.app_name)));
        });

        // Donate
        view.findViewById(R.id.about_donate).setOnClickListener(v -> {
            try {
                if (PaymentUtil.isInstalledPackage(requireContext())) {
                    PaymentUtil.startAlipayClient(requireActivity(), "fkx12941hqcc7gpulzphmee");
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://razorpay.me/@babythings"));
                    startActivity(intent);
                }
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Unable to Open Razorpay Link.", Toast.LENGTH_SHORT).show();
            }
        });

        // GitHub
        view.findViewById(R.id.about_github).setOnClickListener(v ->
                webIntent.launchUrl(requireContext(),
                        Uri.parse("https://github.com/Sourav928893"))
        );

        // Email
        view.findViewById(R.id.about_email).setOnClickListener(v -> {
            Uri uri = Uri.parse("mailto:ittrainer156@gmail.com");
            Intent email = new Intent(Intent.ACTION_SENDTO, uri);
            email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            startActivity(Intent.createChooser(email, "Feedback (E-mail)"));
        });

        // Privacy Policy


        // Open Source Licenses


        // More Apps
        
        // Check Updates
        view.findViewById(R.id.about_app_update).setOnClickListener(v -> {
            appUpdateManager = AppUpdateManagerFactory.create(requireContext());
            appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
            appUpdateInfoTask.addOnSuccessListener(this::handleUpdateInfo);
        });
    }

    private void handleUpdateInfo(AppUpdateInfo info) {
        switch (info.updateAvailability()) {
            case UpdateAvailability.UPDATE_AVAILABLE:
                if (info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    startUpdateFlow(info);
                }
                break;
            case UpdateAvailability.UPDATE_NOT_AVAILABLE:
                Toast.makeText(requireContext(), getString(R.string.newest), Toast.LENGTH_SHORT).show();
                break;
            case UpdateAvailability.UNKNOWN:
            default:
                Toast.makeText(requireContext(), getString(R.string.checkNet), Toast.LENGTH_SHORT).show();
        }
    }

    private void startUpdateFlow(AppUpdateInfo info) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    info,
                    updateLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)
                            .setAllowAssetPackDeletion(true)
                            .build()
            );
        } catch (Exception e) {
            Log.e("AboutFragment", "Failed to start update flow", e);
            Toast.makeText(requireContext(), getString(R.string.checkNet), Toast.LENGTH_SHORT).show();
        }
    }
}
