package com.tsl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class VatCalculatorServiceTest {

    @InjectMocks
    private VatCalculatorService vatCalculatorService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should calculate gross value for a customer from Poland")
    public void testCalculateGrossValue_CustomerFromPoland() {
        BigDecimal netValue = new BigDecimal("100.00");
        String vatNumber = "PL1234567890";

        BigDecimal result = vatCalculatorService.calculateGrossValue(netValue, vatNumber);

        BigDecimal expected = new BigDecimal("123.00");
        assertEquals(expected.setScale(2, BigDecimal.ROUND_HALF_UP),
                result.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    @DisplayName("Should not apply VAT for a customer from Germany")
    public void testCalculateGrossValue_CustomerFromGermany() {
        BigDecimal netValue = new BigDecimal("100.00");
        String vatNumber = "DE123456789";

        BigDecimal result = vatCalculatorService.calculateGrossValue(netValue, vatNumber);

        assertEquals(netValue.setScale(2, BigDecimal.ROUND_HALF_UP),
                result.setScale(2, BigDecimal.ROUND_HALF_UP));
    }
}