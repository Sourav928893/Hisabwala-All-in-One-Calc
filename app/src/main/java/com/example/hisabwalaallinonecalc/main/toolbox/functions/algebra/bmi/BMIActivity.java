package com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.bmi;

import static com.example.hisabwalaallinonecalc.utils.Utils.closeKeyboard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.hisabwalaallinonecalc.R;
import com.example.hisabwalaallinonecalc.databinding.ActivityBmiBinding;
import com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.BaseFunctionActivity;
import com.google.android.material.elevation.SurfaceColors;

public class BMIActivity extends BaseFunctionActivity implements TextWatcher {

    private double heightM = 0, weightKg = 0, bmi = 0, recommendedBmi = 0;
    private String weightToLose = "";
    private ActivityBmiBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRootView(); // Initialize binding first

        // Apply Material background colors
        binding.cardView.setCardBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));
        binding.CardViewRes.setCardBackgroundColor(SurfaceColors.SURFACE_5.getColor(this));

        // Input listeners
        binding.heightCm.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(this);
                binding.heightCm.clearFocus();
                return true;
            }
            return false;
        });
        binding.weightKg.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(this);
                binding.weightKg.clearFocus();
                return true;
            }
            return false;
        });

        // Text change listeners
        binding.heightCm.addTextChangedListener(this);
        binding.weightKg.addTextChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.heightCm.removeTextChangedListener(this);
        binding.weightKg.removeTextChangedListener(this);
    }

    @Override
    protected void setRootView() {
        binding = ActivityBmiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            resetAll();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetAll() {
        resetColor();
        binding.heightCm.setText("");
        binding.weightKg.setText("");
        binding.bmi.setText(R.string._00_00);
        binding.comment.setText("");
        bmi = 0;
        heightM = 0;
        weightKg = 0;
        recommendedBmi = 0;
        weightToLose = "";
        binding.commentLayout.setVisibility(View.GONE);
    }

    @SuppressLint("DefaultLocale")
    private void calculateBmi(double weight, double height) {
        resetColor();
        binding.bmi.setText(R.string._00_00);
        binding.comment.setText("");
        bmi = 0;
        recommendedBmi = 0;
        weightToLose = "";
        binding.commentLayout.setVisibility(View.GONE);

        try {
            weightKg = weight;
            heightM = height / 100;
            if (heightM > 0) {
                bmi = weight / (heightM * heightM);
                binding.bmi.setText(String.format("%.2f", bmi));
            }
        } catch (Exception e) {
            Log.e("BMIActivity", "Error calculating BMI: " + e.getMessage());
        }

        if (bmi > 0) {
            setMessageBackground();
            findRecommendedBmi();
        }
    }

    private void findRecommendedBmi() {
        if (bmi < 18.50) {
            for (int i = 1; i < 100; i++) {
                double newWeight = weightKg + i;
                recommendedBmi = newWeight / (heightM * heightM);
                if (recommendedBmi >= 18.5) {
                    weightToLose = String.valueOf(i);
                    binding.commentLayout.setVisibility(View.VISIBLE);
                    binding.weightNeed.setText(R.string.need_healthy);
                    binding.showKg.setText(R.string.kg);
                    binding.reCommand.setText(weightToLose);
                    break;
                }
            }

        } else if (bmi > 24.90) {
            for (int i = 1; i < 150; i++) {
                double newWeight = weightKg - i;
                recommendedBmi = newWeight / (heightM * heightM);
                if (recommendedBmi <= 24.9) {
                    weightToLose = String.valueOf(i);
                    binding.commentLayout.setVisibility(View.VISIBLE);
                    binding.weightNeed.setText(R.string.lose_healthy);
                    binding.showKg.setText(R.string.kg);
                    binding.reCommand.setText(weightToLose);
                    break;
                }
            }
        } else {
            weightToLose = "0";
            binding.commentLayout.setVisibility(View.VISIBLE);
            binding.weightNeed.setText(R.string.already_healthy);
            binding.showKg.setText("");
            binding.reCommand.setText("");
        }
    }

    private void setMessageBackground() {
        if (bmi < 16.0) {
            binding.comment.setText(R.string.very_severely_underweight);
            binding.verySeverelyUnderweight.setBackgroundColor(ContextCompat.getColor(this, R.color.Very_Severely_underweight));
        } else if (bmi <= 16.99) {
            binding.comment.setText(R.string.severely_underweight);
            binding.severelyUnderweight.setBackgroundColor(ContextCompat.getColor(this, R.color.Severely_underweight));
        } else if (bmi <= 18.49) {
            binding.comment.setText(R.string.underweight);
            binding.underweight.setBackgroundColor(ContextCompat.getColor(this, R.color.Underweight));
        } else if (bmi <= 24.99) {
            binding.comment.setText(R.string.healthy);
            binding.healthy.setBackgroundColor(ContextCompat.getColor(this, R.color.Healthy));
        } else if (bmi <= 29.99) {
            binding.comment.setText(R.string.overweight);
            binding.overweight.setBackgroundColor(ContextCompat.getColor(this, R.color.Overweight));
        } else if (bmi <= 34.99) {
            binding.comment.setText(R.string.obese_class_i);
            binding.obeseClassI.setBackgroundColor(ContextCompat.getColor(this, R.color.Obese_Class_I));
        } else if (bmi <= 39.99) {
            binding.comment.setText(R.string.obese_class_ii);
            binding.obeseClassIi.setBackgroundColor(ContextCompat.getColor(this, R.color.Obese_Class_ii));
        } else {
            binding.comment.setText(R.string.obese_class_iii);
            binding.obeseClassIii.setBackgroundColor(ContextCompat.getColor(this, R.color.Obese_Class_iii));
        }
    }

    private void resetColor() {
        int surfaceColor = SurfaceColors.SURFACE_5.getColor(this);
        binding.verySeverelyUnderweight.setBackgroundColor(surfaceColor);
        binding.severelyUnderweight.setBackgroundColor(surfaceColor);
        binding.underweight.setBackgroundColor(surfaceColor);
        binding.healthy.setBackgroundColor(surfaceColor);
        binding.overweight.setBackgroundColor(surfaceColor);
        binding.obeseClassI.setBackgroundColor(surfaceColor);
        binding.obeseClassIi.setBackgroundColor(surfaceColor);
        binding.obeseClassIii.setBackgroundColor(surfaceColor);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(binding.heightCm.getText()) && !TextUtils.isEmpty(binding.weightKg.getText())) {
            try {
                double height = Double.parseDouble(binding.heightCm.getText().toString());
                double weight = Double.parseDouble(binding.weightKg.getText().toString());
                if (weight > 0 && height > 0) {
                    calculateBmi(weight, height);
                }
            } catch (Exception e) {
                Log.e("BMIActivity", "afterTextChanged: " + e.getMessage());
            }
        }
    }
}
