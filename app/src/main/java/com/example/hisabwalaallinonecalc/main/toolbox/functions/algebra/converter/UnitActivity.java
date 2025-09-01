package com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.converter;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hisabwalaallinonecalc.R;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class UnitActivity extends AppCompatActivity {

    EditText inputValue;
    Spinner spinnerFrom, spinnerTo;
    Button btnConvert;
    TextView tvResult;

    // Simple units table (Example: Length)
    Map<String, Double> lengthUnits = new HashMap<String, Double>() {{
        put("Meter", 1.0);
        put("Kilometer", 1000.0);
        put("Centimeter", 0.01);
        put("Millimeter", 0.001);
        put("Inch", 0.0254);
        put("Foot", 0.3048);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);

        inputValue = findViewById(R.id.inputValue);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        btnConvert = findViewById(R.id.btnConvert);
        tvResult = findViewById(R.id.tvResult);

        // Setup Spinners
        String[] unitArray = lengthUnits.keySet().toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, unitArray);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        // Button click
        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertUnits();
            }
        });
    }

    private void convertUnits() {
        String inputStr = inputValue.getText().toString().trim();
        if (inputStr.isEmpty()) {
            tvResult.setText("Enter a value!");
            return;
        }

        double inputNum = Double.parseDouble(inputStr);
        String fromUnit = spinnerFrom.getSelectedItem().toString();
        String toUnit = spinnerTo.getSelectedItem().toString();

        double fromValue = lengthUnits.get(fromUnit);
        double toValue = lengthUnits.get(toUnit);

        double result = (inputNum * fromValue) / toValue;

        tvResult.setText("Result: " + BigDecimal.valueOf(result).toPlainString() + " " + toUnit);
    }
}
