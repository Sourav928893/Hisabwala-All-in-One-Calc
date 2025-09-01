package com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.finance;

import static com.example.hisabwalaallinonecalc.utils.Utils.closeKeyboard;
import static com.example.hisabwalaallinonecalc.utils.Utils.formatNumberFinance;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hisabwalaallinonecalc.R;
import com.example.hisabwalaallinonecalc.databinding.FragmentBankBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Objects;

public class BankFragment extends Fragment implements TextWatcher {

    private FragmentBankBinding binding;
    private TextInputEditText editTextPrincipal, editTextInterestRate, editTextTime;
    private int flag;

    public BankFragment() { }

    public static BankFragment newInstance() {
        return new BankFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove TextWatchers
        if (editTextPrincipal != null) editTextPrincipal.removeTextChangedListener(this);
        if (editTextInterestRate != null) editTextInterestRate.removeTextChangedListener(this);
        if (editTextTime != null) editTextTime.removeTextChangedListener(this);
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBankBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextPrincipal = binding.editTextPrincipal;
        editTextInterestRate = binding.editTextInterestRate;
        editTextTime = binding.editTextTime;

        editTextPrincipal.addTextChangedListener(this);
        editTextInterestRate.addTextChangedListener(this);
        editTextTime.addTextChangedListener(this);

        editTextTime.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(requireActivity());
                editTextTime.clearFocus();
                return true;
            }
            return false;
        });

        // Setup Spinner
        Spinner spinner = binding.spinnerPeriod;
        String[] arr = new String[]{
                getString(R.string.monthly),
                getString(R.string.quarterly),
                getString(R.string.half),
                getString(R.string.yearly),
                getString(R.string.end)
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, arr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                flag = i;
                calculateInterest();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    @SuppressLint("SetTextI18n")
    private void calculateInterest() {
        String principalStr = Objects.requireNonNull(editTextPrincipal.getText()).toString();
        String rateStr = Objects.requireNonNull(editTextInterestRate.getText()).toString();
        String timeStr = Objects.requireNonNull(editTextTime.getText()).toString();

        if (principalStr.isEmpty() || rateStr.isEmpty() || timeStr.isEmpty()) {
            binding.pieChart.setVisibility(View.INVISIBLE);
            return;
        }

        double principal = Double.parseDouble(principalStr);
        double interestRate = Double.parseDouble(rateStr);
        int time = Integer.parseInt(timeStr);

        double interest = 0;
        int compoundPeriods;

        // ✅ Normal switch instead of Java 14 arrow syntax
        switch (flag) {
            case 0:
                compoundPeriods = 12; // Monthly
                break;
            case 1:
                compoundPeriods = 4; // Quarterly
                break;
            case 2:
                compoundPeriods = 2; // Half Yearly
                break;
            case 3:
                compoundPeriods = 1; // Yearly
                break;
            default:
                compoundPeriods = 0; // End / No compounding
                break;
        }

        if (flag == 4) {
            // Simple Interest at the End
            interest = principal * (interestRate / 100);
        } else {
            double ratePerPeriod = (interestRate / 100) / compoundPeriods;
            interest = principal * Math.pow(1 + ratePerPeriod, compoundPeriods * (time / 12.0)) - principal;
        }

        double total = principal + interest;

        // Setup PieChart
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) interest, getString(R.string.total_interest)));
        entries.add(new PieEntry((float) principal, getString(R.string.principal)));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(20f);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return formatNumberFinance(String.valueOf(value));
            }
        });

        PieChart pieChart = binding.pieChart;
        pieChart.setVisibility(View.VISIBLE);
        pieChart.setData(data);
        pieChart.setCenterText(getString(R.string.settlement_amount) + " " + formatNumberFinance(String.valueOf(total)));
        pieChart.setCenterTextSize(18f);
        pieChart.getDescription().setEnabled(false);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        pieChart.invalidate();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        calculateInterest();
    }
}
