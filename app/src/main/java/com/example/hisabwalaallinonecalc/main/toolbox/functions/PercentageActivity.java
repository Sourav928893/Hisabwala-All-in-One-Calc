package com.example.hisabwalaallinonecalc.main.toolbox.functions;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hisabwalaallinonecalc.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class PercentageActivity extends AppCompatActivity {

    private TextInputEditText percentageInput;
    private TextInputEditText numberInput;
    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percentage);

        percentageInput = findViewById(R.id.percentage_input);
        numberInput = findViewById(R.id.number_input);
        resultView = findViewById(R.id.result_view);

        Button calculateButton = findViewById(R.id.calculate_button);
        Button increaseButton = findViewById(R.id.increase_button);
        Button decreaseButton = findViewById(R.id.decrease_button);

        calculateButton.setOnClickListener(v -> calculatePercentage());
        increaseButton.setOnClickListener(v -> calculateIncrease());
        decreaseButton.setOnClickListener(v -> calculateDecrease());
    }

    private void calculatePercentage() {
        String percentageStr = Objects.requireNonNull(percentageInput.getText()).toString();
        String numberStr = Objects.requireNonNull(numberInput.getText()).toString();

        if (percentageStr.isEmpty() || numberStr.isEmpty()) {
            resultView.setText(R.string.enter_values);
            return;
        }

        try {
            double percentage = Double.parseDouble(percentageStr);
            double number = Double.parseDouble(numberStr);
            double result = (percentage / 100) * number;
            resultView.setText(String.valueOf(result));
        } catch (NumberFormatException e) {
            resultView.setText(R.string.invalid_input);
        }
    }

    private void calculateIncrease() {
        String percentageStr = Objects.requireNonNull(percentageInput.getText()).toString();
        String numberStr = Objects.requireNonNull(numberInput.getText()).toString();

        if (percentageStr.isEmpty() || numberStr.isEmpty()) {
            resultView.setText(R.string.enter_values);
            return;
        }

        try {
            double percentage = Double.parseDouble(percentageStr);
            double number = Double.parseDouble(numberStr);
            double result = number + ((percentage / 100) * number);
            resultView.setText(String.valueOf(result));
        } catch (NumberFormatException e) {
            resultView.setText(R.string.invalid_input);
        }
    }

    private void calculateDecrease() {
        String percentageStr = Objects.requireNonNull(percentageInput.getText()).toString();
        String numberStr = Objects.requireNonNull(numberInput.getText()).toString();

        if (percentageStr.isEmpty() || numberStr.isEmpty()) {
            resultView.setText(R.string.enter_values);
            return;
        }

        try {
            double percentage = Double.parseDouble(percentageStr);
            double number = Double.parseDouble(numberStr);
            double result = number - ((percentage / 100) * number);
            resultView.setText(String.valueOf(result));
        } catch (NumberFormatException e) {
            resultView.setText(R.string.invalid_input);
        }
    }
}