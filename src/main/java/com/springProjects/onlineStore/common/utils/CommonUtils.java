package com.springProjects.onlineStore.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CommonUtils {
    private static final int PRECISION_SCALE = 2; // no. of decimal places

    public static Integer getValueOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    public static Double getValueOrZero(Double value) {
        return value == null ? 0.0 : value;
    }

    public static BigDecimal getPrecisionFixedValueOrZero(BigDecimal value) {
        if(value == null) {
            return BigDecimal.ZERO;
        }
        return value.setScale(PRECISION_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal getDiscountedAmount(BigDecimal actualAmount, BigDecimal discountPercentage) {
        if(actualAmount == null) {
            return BigDecimal.ZERO;
        }
        if(discountPercentage == null) {
            return actualAmount;
        }
        // BigDecimal divide  :  (divisor , scale of quotient [no. of decimal places] , rounding mode for quotient)
        // RoundingMode.HALF_UP  :  behaves as RoundingMode.UP if decimal part >= 0.5  ,  else RoundingMode.DOWN
        BigDecimal discount = actualAmount.multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100), PRECISION_SCALE, RoundingMode.HALF_UP);
        return actualAmount.subtract(discount);
    }
}
