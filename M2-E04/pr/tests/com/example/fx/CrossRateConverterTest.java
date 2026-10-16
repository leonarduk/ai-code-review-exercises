package com.example.fx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CrossRateConverterTest {

    private final CrossRateConverter converter = new CrossRateConverter(Map.of(
            "EUR/USD", new BigDecimal("1.0850"),
            "GBP/USD", new BigDecimal("1.2700"),
            "USD/JPY", new BigDecimal("151.20")));

    @Test
    void convertsToUsd() {
        assertEquals(new BigDecimal("1085.00"),
                converter.convert(new BigDecimal("1000.00"), "EUR", "USD"));
    }

    @Test
    void crossConvertsThroughUsd() {
        // 1,000 GBP = 1,270 USD = 1,170.5069... EUR
        assertEquals(new BigDecimal("1170.51"),
                converter.convert(new BigDecimal("1000.00"), "GBP", "EUR"));
    }

    @Test
    void nonUsdPairsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new CrossRateConverter(Map.of("EUR/GBP", new BigDecimal("0.8543"))));
    }
}
