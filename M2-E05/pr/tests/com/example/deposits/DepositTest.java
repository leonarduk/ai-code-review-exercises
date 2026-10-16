package com.example.deposits;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DepositTest {

    private static Deposit deposit(String principal, String rate, LocalDate start, LocalDate end) {
        return new Deposit(new BigDecimal(principal), new BigDecimal(rate), start, end);
    }

    @Test
    void overnightDeposit() {
        Deposit d = deposit("36000000.00", "0.05", LocalDate.of(2024, 6, 3), LocalDate.of(2024, 6, 4));
        assertEquals(1, d.days());
        assertEquals(new BigDecimal("5000.00"), d.interest());
    }

    @Test
    void oneWeekDeposit() {
        Deposit d = deposit("1000000.00", "0.036", LocalDate.of(2024, 6, 3), LocalDate.of(2024, 6, 10));
        assertEquals(7, d.days());
        assertEquals(new BigDecimal("700.00"), d.interest());
        assertEquals(new BigDecimal("1000700.00"), d.maturityAmount());
    }

    @Test
    void invalidDepositsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> deposit("0", "0.036", LocalDate.of(2024, 6, 3), LocalDate.of(2024, 6, 10)));
        assertThrows(IllegalArgumentException.class,
                () -> deposit("100.00", "0.036", LocalDate.of(2024, 6, 3), LocalDate.of(2024, 6, 3)));
    }
}
