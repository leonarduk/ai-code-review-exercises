package com.example.billing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class InstalmentPlanTest {

    @Test
    void evenSplit() {
        assertEquals(List.of(new BigDecimal("100.00"), new BigDecimal("100.00"), new BigDecimal("100.00")),
                InstalmentPlan.split(new BigDecimal("300.00"), 3));
    }

    @Test
    void lastInstalmentTakesTheRemainder() {
        assertEquals(List.of(new BigDecimal("33.33"), new BigDecimal("33.33"), new BigDecimal("33.34")),
                InstalmentPlan.split(new BigDecimal("100.00"), 3));
    }

    @Test
    void invalidInputIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> InstalmentPlan.split(new BigDecimal("12.345"), 2));
        assertThrows(IllegalArgumentException.class, () -> InstalmentPlan.split(new BigDecimal("100.00"), 0));
    }
}
