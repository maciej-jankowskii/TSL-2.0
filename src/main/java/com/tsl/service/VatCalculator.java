package com.tsl.service;

import java.math.BigDecimal;

public interface VatCalculator {
    BigDecimal calculateGrossValue(BigDecimal netValue, String vatNumber);
}
