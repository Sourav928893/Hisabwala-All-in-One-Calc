package com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.equation;

import static com.example.hisabwalaallinonecalc.utils.Utils.closeKeyboard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hisabwalaallinonecalc.R;
import com.example.hisabwalaallinonecalc.utils.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author 30415
 */
public class QuadraticFragment extends Fragment {

    private EditText aEditText, bEditText, cEditText;
    private TextView x1TextView, x2TextView, equationView;

    public QuadraticFragment() {}

    public static QuadraticFragment newInstance() {
        return new QuadraticFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quadratic, container, false);

        aEditText = view.findViewById(R.id.aEditText);
        bEditText = view.findViewById(R.id.bEditText);
        cEditText = view.findViewById(R.id.cEditText);
        x1TextView = view.findViewById(R.id.x1TextView);
        x2TextView = view.findViewById(R.id.x2TextView);
        equationView = view.findViewById(R.id.equation);

        aEditText.addTextChangedListener(textWatcher);
        bEditText.addTextChangedListener(textWatcher);
        cEditText.addTextChangedListener(textWatcher);
        cEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(requireActivity());
                cEditText.clearFocus();
                return true;
            }
            return false;
        });

        return view;
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable editable) {
            String aValue = aEditText.getText().toString().trim();
            String bValue = bEditText.getText().toString().trim();
            String cValue = cEditText.getText().toString().trim();

            if (aValue.isEmpty() && bValue.isEmpty() && cValue.isEmpty()) {
                x1TextView.setText("");
                x2TextView.setText("");
                equationView.setText("A 𝑥² + B 𝑥 + C = 0");
                return;
            }

            equationView.setText(buildEquation(aValue, bValue, cValue));

            try {
                BigDecimal a = parseBigDecimal(aValue);
                BigDecimal b = parseBigDecimal(bValue);
                BigDecimal c = parseBigDecimal(cValue);

                String result = calculateQuadraticEquation(a, b, c);
                String[] res = new String[2];
                if ("error".equals(result)) {
                    res[0] = getString(R.string.formatError);
                    res[1] = getString(R.string.formatError);
                } else if ("No real roots".equals(result)) {
                    res[0] = getString(R.string.noRoot);
                    res[1] = getString(R.string.noRoot);
                } else {
                    res = result.split(",");
                    res[0] = Utils.formatNumber(res[0]);
                    res[1] = Utils.formatNumber(res[1]);
                }

                x1TextView.setText(res[0]);
                x2TextView.setText(res[1]);
            } catch (Exception ignored) {}
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        aEditText.removeTextChangedListener(textWatcher);
        bEditText.removeTextChangedListener(textWatcher);
        cEditText.removeTextChangedListener(textWatcher);
    }

    private BigDecimal parseBigDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private String calculateQuadraticEquation(BigDecimal a, BigDecimal b, BigDecimal c) {
        if (a.compareTo(BigDecimal.ZERO) == 0) {
            return "error"; // Not a quadratic
        }

        BigDecimal discriminant = b.pow(2).subtract(a.multiply(c).multiply(BigDecimal.valueOf(4)));

        if (discriminant.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal sqrtDiscriminant = sqrt(discriminant);
            BigDecimal x1 = b.negate().add(sqrtDiscriminant)
                    .divide(a.multiply(BigDecimal.valueOf(2)), 10, RoundingMode.HALF_UP);
            BigDecimal x2 = b.negate().subtract(sqrtDiscriminant)
                    .divide(a.multiply(BigDecimal.valueOf(2)), 10, RoundingMode.HALF_UP);
            return x1.toPlainString() + "," + x2.toPlainString();
        } else if (discriminant.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal x = b.negate()
                    .divide(a.multiply(BigDecimal.valueOf(2)), 10, RoundingMode.HALF_UP);
            return x.toPlainString() + "," + x.toPlainString();
        } else {
            return "No real roots";
        }
    }

    private BigDecimal sqrt(BigDecimal value) {
        BigDecimal sqrt = BigDecimal.valueOf(Math.sqrt(value.doubleValue()));
        return sqrt.setScale(10, RoundingMode.HALF_UP);
    }

    private String buildEquation(String aValue, String bValue, String cValue) {
        StringBuilder eq = new StringBuilder();

        eq.append(aValue.isEmpty() ? "A" : aValue).append(" 𝑥² + ");
        eq.append(bValue.isEmpty() ? "B" : bValue).append(" 𝑥 + ");
        eq.append(cValue.isEmpty() ? "C" : cValue);
        eq.append(" = 0");

        return eq.toString();
    }
}
