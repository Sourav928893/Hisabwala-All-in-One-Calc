package com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.converter;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author 30415
 */
public class UnitConverter {

    /**
     * 单位转换 using BigDecimal input
     */
    public static UnitValue convert(String physicalName, String from, String to, BigDecimal value, int scale) {
        Map<String, Double> unitTable = UnitTable.getUnitTable(physicalName);

        if (unitTable == null || !unitTable.containsKey(from) || !unitTable.containsKey(to)) {
            throw new IllegalArgumentException("Invalid unit type: " + from + " or " + to);
        }

        BigDecimal fromValue = BigDecimal.valueOf(unitTable.get(from));
        BigDecimal toValue = BigDecimal.valueOf(unitTable.get(to));

        UnitValue unitValue = new UnitValue();

        // Conversion: first to base, then to target
        value = value.multiply(fromValue);
        value = value.divide(toValue, scale, BigDecimal.ROUND_HALF_UP);

        unitValue.setValue(value);
        unitValue.setUnit(to);

        return unitValue;
    }

    /**
     * Overloaded version with String input/output unit names and double value
     */
    public static UnitValue convert(String unitType, String inputStr, String outputStr, double num, int scale) {
        Map<String, Double> unitTable = UnitTable.getUnitTable(unitType);

        if (unitTable == null || !unitTable.containsKey(inputStr) || !unitTable.containsKey(outputStr)) {
            throw new IllegalArgumentException("Invalid unit type: " + inputStr + " or " + outputStr);
        }

        BigDecimal fromValue = BigDecimal.valueOf(unitTable.get(inputStr));
        BigDecimal toValue = BigDecimal.valueOf(unitTable.get(outputStr));

        BigDecimal value = BigDecimal.valueOf(num);
        value = value.multiply(fromValue);
        value = value.divide(toValue, scale, BigDecimal.ROUND_HALF_UP);

        UnitValue unitValue = new UnitValue();
        unitValue.setValue(value);
        unitValue.setUnit(outputStr);

        return unitValue;
    }
}
