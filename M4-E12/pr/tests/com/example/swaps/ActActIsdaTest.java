package com.example.swaps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ActActIsdaTest {

    private static final FixedLeg LEG = new FixedLeg("IRS-1", "EUR", new BigDecimal("1000000.00"), new BigDecimal("0.04"));

    private static BigDecimal days(int days, int basis) {
        return BigDecimal.valueOf(days).divide(BigDecimal.valueOf(basis), MathContext.DECIMAL128);
    }

    @Test
    void periodInACommonYear() {
        assertEquals(days(181, 365), ActActIsda.yearFraction(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 8, 1)));
    }

    @Test
    void periodInALeapYear() {
        assertEquals(days(182, 366), ActActIsda.yearFraction(LocalDate.of(2028, 2, 1), LocalDate.of(2028, 8, 1)));
    }

    @Test
    void periodEndingOnTheLastDayOfTheYear() {
        assertEquals(days(183, 365), ActActIsda.yearFraction(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 12, 31)));
    }

    @Test
    void emptyPeriod() {
        assertEquals(BigDecimal.ZERO, ActActIsda.yearFraction(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 1)));
    }

    @Test
    void reversedPeriodIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> ActActIsda.yearFraction(LocalDate.of(2026, 3, 2), LocalDate.of(2026, 3, 1)));
    }

    @Test
    void interestRoundedToTheCent() {
        // 1,000,000 x 4% x 184/365 = 20,164.3835...
        assertEquals(new BigDecimal("20164.38"),
                ActActIsda.interest(LEG, LocalDate.of(2026, 3, 15), LocalDate.of(2026, 9, 15)));
    }
}
