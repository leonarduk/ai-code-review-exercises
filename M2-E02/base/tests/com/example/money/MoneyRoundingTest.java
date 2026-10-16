package com.example.money;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyRoundingTest {

    @Test
    void roundsHalfEven() {
        assertEquals(new BigDecimal("2.34"), MoneyRounding.roundCents(new BigDecimal("2.345")));
    }
}
