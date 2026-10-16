package com.example.fees;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MonthlyFeeCalculatorTest {

    @Test
    void monthlyFee() {
        assertEquals(500.0, new MonthlyFeeCalculator(new FeeSchedule(0.006)).monthlyFee(1_000_000), 0.001);
    }
}
