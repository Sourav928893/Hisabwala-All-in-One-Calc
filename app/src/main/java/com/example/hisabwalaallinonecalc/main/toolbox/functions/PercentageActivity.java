package com.example.hisabwalaallinonecalc.main.toolbox.functions;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hisabwalaallinonecalc.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class PercentageActivity extends AppCompatActivity {

    private TextInputEditText percentInput1, valueInput1, valueInput2X, valueInput2Y;
    private TextView result1, result2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percentage);

        // Card 1 Views
        percentInput1 = findViewById(R.id.percent_input1);
        valueInput1 = findViewById(R.id.value_input1);
        result1 = findViewById(R.id.result1);

        // Card 2 Views
        valueInput2X = findViewById(R.id.value_input2_x);
        valueInput2Y = findViewById(R.id.value_input2_y);
        result2 = findViewById(R.id.result2);

        TextWatcher textWatcher1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculatePercentageOfValue();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        percentInput1.addTextChangedListener(textWatcher1);
        valueInput1.addTextChangedListener(textWatcher1);

        TextWatcher textWatcher2 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateWhatPercentOfValue();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        valueInput2X.addTextChangedListener(textWatcher2);
        valueInput2Y.addTextChangedListener(textWatcher2);
    }

    private void calculatePercentageOfValue() {
        String percentStr = Objects.requireNonNull(percentInput1.getText()).toString();
        String valueStr = Objects.requireNonNull(valueInput1.getText()).toString();

        if (percentStr.isEmpty() || valueStr.isEmpty()) {
            result1.setText("Result");
            return;
        }

        try {
            double percent = Double.parseDouble(percentStr);
            double value = Double.parseDouble(valueStr);
            double calculatedResult = (percent / 100) * value;
            result1.setText(String.valueOf(calculatedResult));
        } catch (NumberFormatException e) {
            result1.setText("Invalid input");
        }
    }

    private void calculateWhatPercentOfValue() {
        String valueXStr = Objects.requireNonNull(valueInput2X.getText()).toString();
        String valueYStr = Objects.requireNonNull(valueInput2Y.getText()).toString();

        if (valueXStr.isEmpty() || valueYStr.isEmpty()) {
            result2.setText("Result");
            return;
        }

        try {
            double valueX = Double.parseDouble(valueXStr);
            double valueY = Double.parseDouble(valueYStr);

            if (valueY == 0) {
                result2.setText("Cannot divide by zero");
                return;
            }

            double calculatedResult = (valueX / valueY) * 100;
            result2.setText(String.format("%.2f%%", calculatedResult));
        } catch (NumberFormatException e) {
            result2.setText("Invalid input");
        }
    }
}