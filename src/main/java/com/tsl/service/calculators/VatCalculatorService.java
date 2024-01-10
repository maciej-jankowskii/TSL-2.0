package com.tsl.service.calculators;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class VatCalculatorService implements VatCalculator {
    private static final BigDecimal VAT_RATE_POLAND = new BigDecimal("0.23");

    @Override
    public BigDecimal calculateGrossValue(BigDecimal netValue, String vatNumber) {
        if (vatNumber != null && vatNumber.startsWith("PL")) {
            return netValue.multiply(VAT_RATE_POLAND.add(BigDecimal.ONE));
        } else {
            return netValue;
        }
    }
}
