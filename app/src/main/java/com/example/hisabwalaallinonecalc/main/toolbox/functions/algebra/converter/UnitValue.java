package com.example.hisabwalaallinonecalc.main.toolbox.functions.algebra.converter;

import java.math.BigDecimal;
import androidx.annotation.NonNull;

/**
 * Value with Unit
 * Represents a numeric value tied to a unit (like meters, kg, etc.)
 */
public class UnitValue {
    private BigDecimal value;
    private String unit;

    public UnitValue() {
        super();
    }

    /**
     * @param value 数值
     * @param unit  单位
     */
    public UnitValue(BigDecimal value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /** @noinspection unused */
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        result = prime * result + ((value == null) ? 0 : value.stripTrailingZeros().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        UnitValue other = (UnitValue) obj;

        if (unit == null) {
            if (other.unit != null) return false;
        } else if (!unit.equals(other.unit)) {
            return false;
        }

        return value != null && value.compareTo(other.value) == 0; // BigDecimal safe compare
    }

    @NonNull
    @Override
    public String toString() {
        return value + " " + unit;
    }
}
