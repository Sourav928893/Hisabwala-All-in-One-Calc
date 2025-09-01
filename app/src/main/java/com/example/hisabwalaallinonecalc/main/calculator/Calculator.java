package com.example.hisabwalaallinonecalc.main.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author 30415
 */
public class Calculator {

    // 运算符列表
    private static final List<String> CALC_LIST = Arrays.asList(
            "l", "g", "i", "a", "n", "v", "s", "c", "t", "e", "o", "^", "×", "÷", "+", "-", "(", ")"
    );
    private static final List<Integer> ORDER_LIST = Arrays.asList(
            5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 4, 3, 3, 2, 2, 1, 1
    );
    private static final List<String> SPECIAL_LIST = Arrays.asList(
            "l", "g", "i", "a", "n", "v", "s", "c", "t", "e", "o"
    );
    private static final List<String> OB_SPECIAL = Arrays.asList(
            "ln", "log", "sin⁻¹", "cos⁻¹", "tan⁻¹", "cot⁻¹", "sin", "cos", "tan", "exp", "cot"
    );

    private final boolean useRad;

    public Calculator(boolean rad) {
        this.useRad = rad;
    }

    public BigDecimal calc(String str) {
        String res = calculate(change(str), useRad);
        if (res == null) {
            return null;
        }
        return new BigDecimal(res);
    }

    /**
     * 将中缀表达式转化为后缀表达式
     */
    public List<String> change(String func) {
        for (int i = 0; i < OB_SPECIAL.size(); i++) {
            func = func.replace(OB_SPECIAL.get(i), SPECIAL_LIST.get(i));
        }

        List<String> numList = new ArrayList<>();
        List<String> obList = new ArrayList<>();
        func = func.replace(" ", "");

        String temp;
        int i = 0;
        while (i < func.length()) {
            temp = func.substring(i, i + 1);
            StringBuilder num = new StringBuilder();

            while (!CALC_LIST.contains(temp)) {
                num.append(temp);
                i++;
                try {
                    temp = func.substring(i, i + 1);
                } catch (Exception ex) {
                    break;
                }
            }

            if (!num.toString().isEmpty()) {
                numList.add(num.toString());
            }

            if (i == 0 && "-".equals(temp)) {
                numList.add("0");
            } else if (i != 0 && "-".equals(temp)) {
                if (func.charAt(i - 1) == '(') {
                    numList.add("0");
                }
            }

            if (CALC_LIST.contains(temp)) {
                if (obList.isEmpty()) {
                    obList.add(temp);
                } else {
                    if ("(".equals(temp)) {
                        obList.add(temp);
                    } else if (")".equals(temp)) {
                        String last = obList.get(obList.size() - 1);
                        while (!"(".equals(last)) {
                            numList.add(last);
                            obList.remove(obList.size() - 1);
                            last = obList.get(obList.size() - 1);
                        }
                        obList.remove(obList.size() - 1);
                        try {
                            if (SPECIAL_LIST.contains(obList.get(obList.size() - 1))) {
                                numList.add(obList.remove(obList.size() - 1));
                            }
                        } catch (Exception ignored) {
                        }
                    } else {
                        int order1 = ORDER_LIST.get(CALC_LIST.indexOf(obList.get(obList.size() - 1)));
                        int order2 = ORDER_LIST.get(CALC_LIST.indexOf(temp));
                        if (order2 > order1) {
                            obList.add(temp);
                        } else {
                            while (order2 <= order1) {
                                numList.add(obList.remove(obList.size() - 1));
                                if (obList.isEmpty()) break;
                                order1 = ORDER_LIST.get(CALC_LIST.indexOf(obList.get(obList.size() - 1)));
                            }
                            obList.add(temp);
                        }
                    }
                }
                i++;
            }
        }

        while (!obList.isEmpty()) {
            numList.add(obList.remove(obList.size() - 1));
        }
        return numList;
    }

    public BigDecimal division(BigDecimal i1, BigDecimal i2) {
        return i1.divide(i2, 15, RoundingMode.HALF_UP);
    }

    public double cot(double i2) {
        return (1 / Math.tan(i2));
    }

    public double acot(double i2) {
        return (Math.PI / 2 - Math.atan(i2));
    }

    /**
     * 计算后缀表达式
     */
    public String calculate(List<String> list, boolean useDeg) {
        Deque<String> numStack = new LinkedList<>();

        for (String s : list) {
            if (CALC_LIST.contains(s)) {
                BigDecimal result;
                if (SPECIAL_LIST.contains(s)) {
                    result = BigDecimal.valueOf(calculateSpecialFunction(numStack.pop(), s, useDeg));
                } else {
                    result = calculateOperator(numStack.pop(), numStack.pop(), s);
                    if (result == null) {
                        return null;
                    }
                }
                numStack.push(result.toPlainString());
            } else {
                numStack.push(s);
            }
        }
        return numStack.pop();
    }

    private double calculateSpecialFunction(String number, String function, boolean useDeg) {
        double num = Double.parseDouble(number);
        double numRad = num;

        if (!useDeg) { // if rad = false → convert to radians
            numRad = Math.toRadians(num);
        }

        switch (function) {
            case "s": return Math.sin(numRad);
            case "c": return Math.cos(numRad);
            case "t": return Math.tan(numRad);
            case "o": return cot(numRad);
            case "i": return Math.asin(numRad);
            case "a": return Math.acos(numRad);
            case "n": return Math.atan(numRad);
            case "v": return acot(numRad);
            case "e": return Math.exp(num);
            case "l": return Math.log(num);
            case "g": return Math.log10(num);
            default:  return 0;
        }
    }

    private BigDecimal calculateOperator(String number1, String number2, String operator) {
        BigDecimal num2 = new BigDecimal(number1);
        BigDecimal num1 = new BigDecimal(number2);

        switch (operator) {
            case "+": return num1.add(num2);
            case "-": return num1.subtract(num2);
            case "×": return num1.multiply(num2);
            case "÷": return division(num1, num2);
            case "^":
                if (num2.compareTo(BigDecimal.ZERO) == 0) {
                    return BigDecimal.ONE;
                } else if (num2.compareTo(BigDecimal.ONE) == 0) {
                    return num1;
                } else if (num2.compareTo(BigDecimal.ONE) < 0) {
                    return BigDecimal.valueOf(Math.pow(num1.doubleValue(), num2.doubleValue()));
                } else {
                    if (num2.compareTo(BigDecimal.valueOf(1000)) < 0) {
                        if (num2.scale() <= 0) {
                            return num1.pow(num2.intValue());
                        } else {
                            return BigDecimal.valueOf(Math.pow(num1.doubleValue(), num2.doubleValue()));
                        }
                    } else {
                        return null;
                    }
                }
            default: return BigDecimal.ZERO;
        }
    }
}
