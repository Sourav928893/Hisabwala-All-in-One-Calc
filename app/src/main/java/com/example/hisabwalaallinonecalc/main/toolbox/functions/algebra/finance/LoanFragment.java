package com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.finance;

import static com.example.hisabwalaallinonecalc.utils.Utils.formatNumberFinance;
import static com.example.hisabwalaallinonecalc.utils.Utils.closeKeyboard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hisabwalaallinonecalc.R;
import com.example.hisabwalaallinonecalc.databinding.FragmentLoanBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author 30415
 */
public class LoanFragment extends Fragment implements TextWatcher {

    private TextInputEditText etLoanAmount, etInterestRate, etLoanPeriod;
    private FragmentLoanBinding binding;

    public LoanFragment() {
    }

    public static LoanFragment newInstance() {
        return new LoanFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (etLoanAmount != null) etLoanAmount.removeTextChangedListener(this);
        if (etInterestRate != null) etInterestRate.removeTextChangedListener(this);
        if (etLoanPeriod != null) etLoanPeriod.removeTextChangedListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etLoanAmount = view.findViewById(R.id.etLoanAmount);
        etInterestRate = view.findViewById(R.id.etInterestRate);
        etLoanPeriod = view.findViewById(R.id.etLoanPeriod);

        etLoanPeriod.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(requireActivity());
                etLoanPeriod.clearFocus();
                return true;
            }
            return false;
        });

        etLoanAmount.addTextChangedListener(this);
        etInterestRate.addTextChangedListener(this);
        etLoanPeriod.addTextChangedListener(this);
    }

    private void calculateLoan() {
        double loanAmount = Double.parseDouble(Objects.requireNonNull(etLoanAmount.getText()).toString());
        double interestRate = Double.parseDouble(Objects.requireNonNull(etInterestRate.getText()).toString()) / 100;
        int loanPeriod = Integer.parseInt(Objects.requireNonNull(etLoanPeriod.getText()).toString());

        double monthlyInterestRate = interestRate / 12;
        double monthlyPayment = (loanAmount * monthlyInterestRate) /
                (1 - Math.pow(1 + monthlyInterestRate, -loanPeriod));
        double totalInterest = (monthlyPayment * loanPeriod) - loanAmount;
        double totalAmount = monthlyPayment * loanPeriod;

        float interestValue = (float) totalInterest;
        float totalAmountValue = (float) totalAmount;

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(interestValue, getString(R.string.total_interest)));
        entries.add(new PieEntry(totalAmountValue, getString(R.string.total_amount)));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(16);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return formatNumberFinance(String.valueOf(value));
            }
        });

        PieChart pieChart = binding.pieChart;
        pieChart.setVisibility(View.VISIBLE);
        pieChart.setData(data);
        pieChart.setCenterText(getString(R.string.monthly_payment) + " " +
                formatNumberFinance(String.valueOf(monthlyPayment)));
        pieChart.getDescription().setEnabled(false);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        pieChart.invalidate();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void afterTextChanged(Editable editable) {
        try {
            calculateLoan();
        } catch (Exception ignored) { }
    }
}
