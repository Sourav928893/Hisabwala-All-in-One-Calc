package com.example.hisabwalaallinonecalc.main.calculator;

import static com.example.hisabwalaallinonecalc.main.calculator.CalculatorUtils.calculateAllFactorial;
import static com.example.hisabwalaallinonecalc.main.calculator.CalculatorUtils.optimizePercentage;
import static com.example.hisabwalaallinonecalc.utils.Utils.formatNumber;
import static com.example.hisabwalaallinonecalc.utils.Utils.isNumber;
import static com.example.hisabwalaallinonecalc.utils.Utils.isNumeric;
import static com.example.hisabwalaallinonecalc.utils.Utils.isSymbol;
import static com.example.hisabwalaallinonecalc.utils.Utils.isSymbolForDot;
import static com.example.hisabwalaallinonecalc.utils.Utils.removeZeros;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hisabwalaallinonecalc.utils.TTS;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Calculator ViewModel with TTS support
 */
public class CalculatorViewModel extends ViewModel {

    private final MutableLiveData<String> expression = new MutableLiveData<>();
    private final MutableLiveData<String> result = new MutableLiveData<>();
    private int left = 0, right = 0;

    public LiveData<String> getInputTextState() {
        return expression;
    }

    public LiveData<String> getOutputTextState() {
        return result;
    }

    /** Handle factorial */
    @SuppressLint("SetTextI18n")
    public void handleFactorial(String inputStr, boolean canSpeak, TTS tts,
                                String factorialWord, String doubleFactorialWord) {
        if (!inputStr.isEmpty()) {
            char lastChar = inputStr.charAt(inputStr.length() - 1);
            if (isNumber(String.valueOf(lastChar)) && lastChar != 'e' && lastChar != 'π') {
                for (int i = inputStr.length() - 1; i >= 0; i--) {
                    if (inputStr.charAt(i) == '.') return;
                    if (isSymbol(String.valueOf(inputStr.charAt(i)))) {
                        expression.setValue(inputStr + "!");
                        if (canSpeak && tts != null) tts.ttsSpeak(factorialWord);
                        return;
                    }
                }
                expression.setValue(inputStr + "!");
                if (canSpeak && tts != null) tts.ttsSpeak(factorialWord);
            } else if (lastChar == '!') {
                char secondLastChar = inputStr.charAt(inputStr.length() - 2);
                if (secondLastChar != '!') {
                    expression.setValue(inputStr + "!");
                    if (canSpeak && tts != null) tts.ttsSpeak(doubleFactorialWord);
                }
            }
        }
    }

    /** Handle = button */
    @SuppressLint("SetTextI18n")
    public void handleEqualButton(String inputStr,
                                  Calculator formulaUtil,
                                  SharedPreferences defaultSp,
                                  SharedPreferences history,
                                  boolean fromUser,
                                  String bigNum,
                                  String error) {
        if ("e".equals(inputStr) || "π".equals(inputStr)) {
            inputStr = inputStr.replace("e", String.valueOf(Math.E))
                    .replace("π", String.valueOf(Math.PI));
            result.setValue(inputStr);
            return;
        }

        if (inputStr.isEmpty() || isNumeric(inputStr)) {
            result.setValue("");
            return;
        }

        if (isSymbol(String.valueOf(inputStr.charAt(inputStr.length() - 1)))) {
            inputStr += "0";
        } else if (isSymbol(String.valueOf(inputStr.charAt(0))) || inputStr.charAt(0) == '%') {
            inputStr = "0" + inputStr;
        }

        if (left != right) {
            int addCount = Math.abs(left - right);
            StringBuilder inputStrBuilder = new StringBuilder(inputStr);
            for (int j = 0; j < addCount; j++) {
                inputStrBuilder.append(")");
            }
            inputStr = inputStrBuilder.toString();
        }

        inputStr = optimizePercentage(inputStr);

        try {
            if (inputStr.contains("!")) {
                inputStr = calculateAllFactorial(inputStr);
                if ("数值过大".equals(inputStr)) {
                    result.setValue(bigNum);
                    return;
                }
            }

            Matcher matcher = Pattern.compile("\\be\\b").matcher(inputStr);
            inputStr = matcher.replaceAll(String.valueOf(Math.E));
            inputStr = inputStr.replace("π", String.valueOf(Math.PI))
                    .replace("%", "÷100");

            BigDecimal bigDecimal = formulaUtil.calc(inputStr);
            if (bigDecimal == null) {
                result.setValue(bigNum);
                return;
            }

            bigDecimal = bigDecimal.setScale(defaultSp.getInt("scale", 10), BigDecimal.ROUND_HALF_UP);
            String res = bigDecimal.toPlainString();
            res = removeZeros(res);

            if (fromUser) {
                String historys = history.getString("newHistory", "");
                List<String> savedStringList = new ArrayList<>(Arrays.asList(historys.split("//")));

                if (savedStringList.size() >= defaultSp.getInt("historyNum", 100)) {
                    int removeCount = savedStringList.size() - defaultSp.getInt("historyNum", 100) + 1;
                    savedStringList.removeAll(savedStringList.subList(0, removeCount));
                }
                savedStringList.add(inputStr + "\n" + "=" + res);

                String listString = TextUtils.join("//", savedStringList);
                history.edit().putString("newHistory", listString).apply();

                expression.setValue(res);
                result.setValue("");
                left = 0;
                right = 0;
            } else {
                result.setValue(formatNumber(res));
            }
        } catch (Exception e) {
            if (fromUser) {
                result.setValue(error);
            }
        }
    }

    public void handleCleanButton() {
        expression.setValue("");
        result.setValue("");
        left = 0;
        right = 0;
    }

    public void handleDeleteButton(String inputStr) {
        if (!inputStr.isEmpty()) {
            if (inputStr.endsWith("sin⁻¹(") || inputStr.endsWith("cos⁻¹(")
                    || inputStr.endsWith("tan⁻¹(") || inputStr.endsWith("cot⁻¹(")) {
                inputStr = inputStr.substring(0, inputStr.length() - 6);
                left--;
            } else if (inputStr.endsWith("sin(") || inputStr.endsWith("cos(") || inputStr.endsWith("exp(")
                    || inputStr.endsWith("tan(") || inputStr.endsWith("cot(") || inputStr.endsWith("log(")) {
                inputStr = inputStr.substring(0, inputStr.length() - 4);
                left--;
            } else if (inputStr.endsWith("ln(")) {
                inputStr = inputStr.substring(0, inputStr.length() - 3);
                left--;
            } else {
                char lastChar = inputStr.charAt(inputStr.length() - 1);
                if (lastChar == ')') right--;
                if (lastChar == '(') left--;
                inputStr = inputStr.substring(0, inputStr.length() - 1);
            }
            expression.setValue(inputStr);
        }
        if (inputStr.isEmpty()) result.setValue("");
    }

    @SuppressLint("SetTextI18n")
    public void handleBracketsButton(String inputStr) {
        if (!inputStr.isEmpty()) {
            char lastChar = inputStr.charAt(inputStr.length() - 1);
            if (left > right && (isNumber(String.valueOf(lastChar))
                    || lastChar == '!' || lastChar == '%' || lastChar == ')')) {
                expression.setValue(inputStr + ")");
                right++;
                return;
            } else if (lastChar == ')' || isNumber(String.valueOf(lastChar))) {
                expression.setValue(inputStr + "×(");
            } else {
                expression.setValue(inputStr + "(");
            }
        } else {
            expression.setValue("(");
        }
        left++;
    }

    @SuppressLint("SetTextI18n")
    public void handleInverseButton(String inputStr) {
        if (!inputStr.isEmpty()) {
            char lastChar = inputStr.charAt(inputStr.length() - 1);
            if (isNumber(String.valueOf(lastChar))) {
                StringBuilder n = new StringBuilder();
                n.insert(0, lastChar);
                if (inputStr.length() > 1) {
                    for (int i = inputStr.length() - 2; i >= 0; i--) {
                        char curr = inputStr.charAt(i);
                        if (isNumber(String.valueOf(curr)) || curr == '.') {
                            n.insert(0, curr);
                        } else {
                            if (curr == '-' && i >= 1 && "(-".equals(inputStr.substring(i - 1, i + 1))) {
                                inputStr = inputStr.substring(0, i - 1);
                                expression.setValue(inputStr + n);
                                left--;
                                return;
                            }
                            inputStr = inputStr.substring(0, i + 1);
                            String prefix = (curr == ')') ? "×(-" : "(-";
                            expression.setValue(inputStr + prefix + n);
                            left++;
                            return;
                        }
                    }
                }
                expression.setValue("(-" + n);
                left++;
                return;
            } else if (lastChar == '-') {
                if (inputStr.length() > 1 && (inputStr.charAt(inputStr.length() - 2) == '(')) {
                    expression.setValue(inputStr.substring(0, inputStr.length() - 2));
                    left--;
                    return;
                }
            }
            String prefix = (lastChar == ')' || lastChar == '!') ? "×(-" : "(-";
            expression.setValue(inputStr + prefix);
        } else {
            expression.setValue("(-");
        }
        left++;
    }

    /** ✅ Unified handleOtherButtons with TTS */
    @SuppressLint("SetTextI18n")
    public void handleOtherButtons(View v, String inputStr, boolean canSpeak, TTS tts, boolean fromUser) {
        String append = ((MaterialButton) v).getText().toString();

        if (fromUser && isNumber(append)) {
            expression.setValue(append);
        } else {
            if (!inputStr.isEmpty()) {
                char lastInput = inputStr.charAt(inputStr.length() - 1);

                if (".".equals(append)) {
                    if (lastInput == 'e' || lastInput == 'π') return;
                    int lastSymbolIndex = -1;
                    for (int i = inputStr.length() - 1; i >= 0; i--) {
                        if (isSymbolForDot(String.valueOf(inputStr.charAt(i)))) {
                            lastSymbolIndex = i;
                            break;
                        }
                    }
                    String currentNumber = inputStr.substring(lastSymbolIndex + 1);
                    if (currentNumber.isEmpty() || currentNumber.contains(".")) return;
                }

                if (isNumber(append)) {
                    if (")!%eπ".contains(String.valueOf(lastInput))) {
                        expression.setValue(inputStr + "×" + append);
                        if (canSpeak && tts != null) tts.ttsSpeak(append);
                        return;
                    }
                }

                if (isSymbol(String.valueOf(lastInput)) && isSymbol(append)) {
                    expression.setValue(inputStr.substring(0, inputStr.length() - 1) + append);
                    if (canSpeak && tts != null) tts.ttsSpeak(append);
                    return;
                }

                if (isNumber(String.valueOf(lastInput)) && ("e".equals(append) || "π".equals(append))) {
                    expression.setValue(inputStr + "×" + append);
                    if (canSpeak && tts != null) tts.ttsSpeak(append);
                    return;
                }
            }

            if ("sin cos tan cot sin⁻¹ cos⁻¹ tan⁻¹ cot⁻¹ log ln exp".contains(append)) {
                if (!inputStr.isEmpty()) {
                    char lastInput = inputStr.charAt(inputStr.length() - 1);
                    if (isNumber(String.valueOf(lastInput)) || ")!%".contains(String.valueOf(lastInput))) {
                        expression.setValue(inputStr + "×" + append + "(");
                        left++;
                        if (canSpeak && tts != null) tts.ttsSpeak(append);
                        return;
                    }
                }
                expression.setValue(inputStr + append + "(");
                left++;
                if (canSpeak && tts != null) tts.ttsSpeak(append);
                return;
            }
            expression.setValue(inputStr + append);
        }

        if (canSpeak && tts != null) tts.ttsSpeak(append);
    }
}
