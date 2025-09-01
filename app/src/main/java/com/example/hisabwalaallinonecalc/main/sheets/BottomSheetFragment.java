package com.example.hisabwalaallinonecalc.main.sheets;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hisabwalaallinonecalc.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Objects;

/**
 * A fragment that shows a list of items as a modal bottom sheet.
 * You can show this modal bottom sheet from your activity like this:
 * BottomSheetFragment.newInstance().show(getSupportFragmentManager(), "dialog");
 */
public class BottomSheetFragment extends BottomSheetDialogFragment {

    private AboutFragment aboutFragment;

    public BottomSheetFragment() {
        aboutFragment = AboutFragment.newInstance();
    }

    public static BottomSheetFragment newInstance() {
        return new BottomSheetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FrameLayout settingsContainer = view.findViewById(R.id.settings);
        ChipGroup chipGroup = view.findViewById(R.id.chipGroup);
        Chip chip1 = view.findViewById(R.id.chip1);
        Chip chip2 = view.findViewById(R.id.chip2);
        ImageView closeButton = view.findViewById(R.id.close_button);

        // Initially load SettingsFragment
        getChildFragmentManager().beginTransaction()
                .replace(settingsContainer.getId(), SettingsFragment.newInstance())
                .commit();

        // Close button
        closeButton.setOnClickListener(v -> dismiss());

        // Chip selection listener
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                if (checkedIds.get(0) == chip1.getId()) {
                    getChildFragmentManager().beginTransaction()
                            .replace(settingsContainer.getId(), SettingsFragment.newInstance())
                            .commit();
                } else if (checkedIds.get(0) == chip2.getId()) {
                    getChildFragmentManager().beginTransaction()
                            .replace(settingsContainer.getId(), aboutFragment)
                            .commit();
                }
            }
        });

        // Adjust peek height and handle expanded/collapsed state
        Objects.requireNonNull(getDialog()).setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet == null) return;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels;

            int peekHeight = screenHeight * 10 / 21; // Customize peek height
            BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setPeekHeight(peekHeight);
            behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    closeButton.setVisibility(newState == BottomSheetBehavior.STATE_EXPANDED ?
                            View.VISIBLE : View.INVISIBLE);
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    // Optional: handle slide offset
                }
            });
        });
    }
}
