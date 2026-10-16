package com.example.money;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Rounding of monetary amounts. */
public final class MoneyRounding {

    private MoneyRounding() {
    }

    /** Rounds {@code amount} to the cent, half-even. */
    public static BigDecimal roundCents(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_EVEN);
    }
}
