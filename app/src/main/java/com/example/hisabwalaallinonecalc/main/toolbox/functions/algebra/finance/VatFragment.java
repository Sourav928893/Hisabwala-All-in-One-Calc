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
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hisabwalaallinonecalc.R;

import com.example.hisabwalaallinonecalc.databinding.FragmentVatBinding;
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

public class VatFragment extends Fragment implements TextWatcher {

    private TextInputEditText editTextAmount, editTextVatRate;
    private CheckBox checkBoxVatIncluded;
    private FragmentVatBinding binding;

    public VatFragment() { }

    public static VatFragment newInstance() {
        return new VatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        editTextAmount.removeTextChangedListener(this);
        editTextVatRate.removeTextChangedListener(this);
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextAmount = binding.editTextAmount;
        editTextVatRate = binding.editTextVatRate;
        checkBoxVatIncluded = binding.checkBoxVatIncluded;

        editTextAmount.addTextChangedListener(this);
        editTextVatRate.addTextChangedListener(this);

        editTextVatRate.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(requireActivity());
                editTextVatRate.clearFocus();
                return true;
            }
            return false;
        });

        checkBoxVatIncluded.setOnCheckedChangeListener((buttonView, isChecked) -> calculateVat());
    }

    @SuppressLint("SetTextI18n")
    private void calculateVat() {
        String amountStr = Objects.requireNonNull(editTextAmount.getText()).toString();
        String vatRateStr = Objects.requireNonNull(editTextVatRate.getText()).toString();

        if (amountStr.isEmpty() || vatRateStr.isEmpty()) {
            if (binding != null && binding.pieChart != null) binding.pieChart.setVisibility(View.INVISIBLE);
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            double vatRate = Double.parseDouble(vatRateStr);
            boolean vatIncluded = checkBoxVatIncluded.isChecked();

            double vatAmount;
            double totalAmount;
            double baseAmount;

            if (vatIncluded) {
                baseAmount = amount / (1 + vatRate / 100);
                vatAmount = amount - baseAmount;
                totalAmount = amount;
            } else {
                vatAmount = amount * vatRate / 100;
                baseAmount = amount;
                totalAmount = amount + vatAmount;
            }

            // PieChart entries
            ArrayList<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry((float) vatAmount, getString(R.string.vat)));
            entries.add(new PieEntry((float) baseAmount, getString(R.string.taxExcludedAmount)));

            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            PieData data = new PieData(dataSet);
            data.setValueTextSize(26);
            data.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return formatNumberFinance(String.valueOf(value));
                }
            });

            PieChart pieChart = binding.pieChart;
            pieChart.setVisibility(View.VISIBLE);
            pieChart.setData(data);
            pieChart.setCenterText(getString(R.string.taxIncludedAmount) + " " + formatNumberFinance(String.valueOf(totalAmount)));
            pieChart.getDescription().setEnabled(false);

            Legend legend = pieChart.getLegend();
            legend.setEnabled(false);

            pieChart.invalidate();

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), getString(R.string.formatError), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        calculateVat();
    }
}
