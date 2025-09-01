// MainActivity.java
package com.example.hisabwalaallinonecalc.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hisabwalaallinonecalc.R;
import com.example.hisabwalaallinonecalc.features.FloatingWindow;
import com.example.hisabwalaallinonecalc.main.sheets.BottomSheetFragment;
import com.example.hisabwalaallinonecalc.main.toolbox.ToolBoxFragment;
import com.example.hisabwalaallinonecalc.utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Menu menu;
    private ViewPager2 viewPager;
    private int currentPosition = 0;
    private ImageView pageIcon;
    private SharedPreferences defaultSharedPrefs;

    // ✅ Your real AdMob Banner Ad Unit ID
    private static final String AD_UNIT_ID = "ca-app-pub-4041401840560784/5739586189";

    private AdView adView;
    private FrameLayout adContainerView;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menu = menu;

        if (currentPosition == 1) {
            menu.findItem(R.id.historys).setVisible(false);
            menu.findItem(R.id.view_layout).setVisible(true);
        } else {
            menu.findItem(R.id.historys).setVisible(true);
            menu.findItem(R.id.view_layout).setVisible(false);
        }

        boolean isGrid = defaultSharedPrefs.getBoolean("GridLayout", true);
        menu.findItem(R.id.view_layout)
                .setIcon(getDrawable(isGrid ? R.drawable.grid_on : R.drawable.table_rows));

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.historys) {
            if (viewPager != null) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() == 0 ? 1 : 0);
            }
            return true;

        } else if (item.getItemId() == R.id.resize) {
            if (!Settings.canDrawOverlays(this)) {
                requestOverlayDisplayPermission();
            } else {
                ContextCompat.startForegroundService(
                        this, new Intent(MainActivity.this, FloatingWindow.class));
                finish();
            }

        } else if (item.getItemId() == R.id.setting) {
            BottomSheetFragment.newInstance().show(getSupportFragmentManager(), "dialog");

        } else if (item.getItemId() == R.id.view_layout) {
            boolean isGrid = defaultSharedPrefs.getBoolean("GridLayout", true);
            defaultSharedPrefs.edit().putBoolean("GridLayout", !isGrid).apply();

            item.setIcon(getDrawable(!isGrid ? R.drawable.grid_on : R.drawable.table_rows));

            Bundle result = new Bundle();
            result.putBoolean("GridLayout", !isGrid);
            getSupportFragmentManager().setFragmentResult("ChangeLayout", result);
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestOverlayDisplayPermission() {
        new MaterialAlertDialogBuilder(this)
                .setCancelable(true)
                .setTitle(getString(R.string.Screen_Overlay_Permission_Needed))
                .setMessage(getString(R.string.Permission_Dialog_Messege))
                .setNegativeButton(android.R.string.cancel,
                        (dialog, which) -> Toast.makeText(MainActivity.this, getString(R.string.permission), Toast.LENGTH_SHORT).show())
                .setPositiveButton(R.string.Open_Settings,
                        (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            overlayPermissionLauncher.launch(intent);
                        }).show();
    }

    private final ActivityResultLauncher<Intent> overlayPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (Settings.canDrawOverlays(this)) {
                            ContextCompat.startForegroundService(
                                    this, new Intent(MainActivity.this, FloatingWindow.class));
                            finish();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ✅ Setup AdMob Banner with your ID
        adContainerView = findViewById(R.id.ad_container);
        MobileAds.initialize(this, initializationStatus -> {});
        adView = new AdView(this);
        adView.setAdUnitId(AD_UNIT_ID);
        AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, 360);
        adView.setAdSize(adSize);
        adContainerView.removeAllViews();
        adContainerView.addView(adView);
        adView.loadAd(new AdRequest.Builder().build());

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.i("AdMob", "✅ Ad loaded successfully.");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                Log.e("AdMob", "❌ Ad failed: " + adError.getMessage());
            }
        });

        // Preferences
        defaultSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        defaultSharedPrefs.registerOnSharedPreferenceChangeListener(this);

        if (defaultSharedPrefs.getBoolean("screen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        setupToolbar();
        setupViewPager();

        pageIcon.setOnClickListener(v -> {
            currentPosition = currentPosition == 0 ? 1 : 0;
            viewPager.setCurrentItem(currentPosition, true);
        });
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(SurfaceColors.SURFACE_0.getColor(this)));
        getSupportActionBar().setElevation(0f);

        pageIcon = findViewById(R.id.page_icon);
    }

    private void setupViewPager() {
        WormDotsIndicator dotsIndicator = findViewById(R.id.dotsIndicator);
        viewPager = findViewById(R.id.view_pager_main);

        if (viewPager != null) {
            reduceDragSensitivity();
            List<Fragment> fragments = new ArrayList<>();
            fragments.add(MainFragment.newInstance());
            fragments.add(ToolBoxFragment.newInstance());

            MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), getLifecycle(), fragments);
            viewPager.setAdapter(pagerAdapter);

            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onPageSelected(int position) {
                    currentPosition = position;
                    if (menu != null) {
                        menu.findItem(R.id.historys).setVisible(position == 0);
                        menu.findItem(R.id.view_layout).setVisible(position == 1);
                    }
                    pageIcon.setImageDrawable(getDrawable(position == 0 ?
                            R.drawable.calculate_icon : R.drawable.grid_view_more));
                }
            });

            dotsIndicator.attachTo(viewPager);
        }
    }

    private void reduceDragSensitivity() {
        try {
            Field ff = ViewPager2.class.getDeclaredField("mRecyclerView");
            ff.setAccessible(true);
            RecyclerView recyclerView = (RecyclerView) ff.get(viewPager);

            Field touchSlopField = RecyclerView.class.getDeclaredField("mTouchSlop");
            touchSlopField.setAccessible(true);
            Integer touchSlop = (Integer) touchSlopField.get(recyclerView);

            if (touchSlop != null) {
                touchSlopField.set(recyclerView, touchSlop * 5);
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error reducing drag sensitivity", e);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        if (defaultSharedPrefs.getBoolean("vib", false)) {
            Utils.vibrate(this);
        }
        if ("split".equals(key)) {
            Toast.makeText(this, getString(R.string.restart), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }
        defaultSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }
}
