package com.example.financing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class FinancingChargeTest {

    @Test
    void oneNightMidweek() {
        assertEquals(1, FinancingCharge.nights(LocalDate.of(2026, 6, 9), LocalDate.of(2026, 6, 10)));
    }

    @Test
    void aWeekendIsThreeNights() {
        assertEquals(3, FinancingCharge.nights(LocalDate.of(2026, 6, 12), LocalDate.of(2026, 6, 15)));
    }

    @Test
    void sterlingChargeIsActual365() {
        Position p = new Position("P-1", "GBP", new BigDecimal("10000000.00"));
        assertEquals(new BigDecimal("4109.59"), FinancingCharge.charge(p, new BigDecimal("0.05"),
                LocalDate.of(2026, 6, 12), LocalDate.of(2026, 6, 15)));
    }

    @Test
    void dollarChargeIsActual360() {
        Position p = new Position("P-2", "USD", new BigDecimal("3600000.00"));
        assertEquals(new BigDecimal("1000.00"), FinancingCharge.charge(p, new BigDecimal("0.05"),
                LocalDate.of(2026, 6, 9), LocalDate.of(2026, 6, 11)));
    }

    @Test
    void closingBeforeOpeningIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> FinancingCharge.nights(LocalDate.of(2026, 6, 10), LocalDate.of(2026, 6, 9)));
    }
}
