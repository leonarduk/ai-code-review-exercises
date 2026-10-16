package com.example.settlement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SettlementSummaryTest {

    @Test
    void netAmountsAreTotalledPerTaxRate() {
        Map<BigDecimal, BigDecimal> totals = SettlementSummary.netByTaxRate(List.of(
                "T-1001,1250.00,0.20",
                "T-1002,750.00,0.20",
                "T-1003,99.99,0.05"));
        assertEquals(new BigDecimal("2000.00"), totals.get(new BigDecimal("0.20")));
        assertEquals(new BigDecimal("99.99"), totals.get(new BigDecimal("0.05")));
        assertEquals(2, totals.size());
    }

    @Test
    void parsesSurroundingWhitespace() {
        assertEquals(new BigDecimal("12.50"), SettlementSummary.parseDecimal(" 12.50 "));
    }

    @Test
    void lineWithTheWrongNumberOfFieldsFailsTheFile() {
        assertThrows(SettlementFileException.class,
                () -> SettlementSummary.netByTaxRate(List.of("T-1004,1,250.00,0.20")));
    }
}
