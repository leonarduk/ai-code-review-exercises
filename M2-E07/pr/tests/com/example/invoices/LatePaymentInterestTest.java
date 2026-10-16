package com.example.invoices;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class LatePaymentInterestTest {

    private final LatePaymentInterest interest =
            new LatePaymentInterest(new StatutoryRates(0.0825));

    @Test
    void interestForThirtyDaysLate() {
        // 10,000.00 x 8.25% x 30 / 365 = 67.808...
        assertEquals(new BigDecimal("67.81"), interest.interest(
                new BigDecimal("10000.00"), LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31)));
    }

    @Test
    void noInterestWhenPaidOnTime() {
        assertEquals(new BigDecimal("0.00"), interest.interest(
                new BigDecimal("10000.00"), LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 1)));
        assertEquals(new BigDecimal("0.00"), interest.interest(
                new BigDecimal("10000.00"), LocalDate.of(2024, 5, 1), LocalDate.of(2024, 4, 20)));
    }

    @Test
    void noInterestOnACreditBalance() {
        assertEquals(new BigDecimal("0.00"), interest.interest(
                new BigDecimal("-50.00"), LocalDate.of(2024, 5, 1), LocalDate.of(2024, 6, 1)));
    }
}
