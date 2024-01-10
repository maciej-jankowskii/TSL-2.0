package com.tsl.service.calculators;

import java.math.BigDecimal;

public interface VatCalculator {
    BigDecimal calculateGrossValue(BigDecimal netValue, String vatNumber);
}
