package com.example.swaps;

import java.math.BigDecimal;
import java.util.Objects;

/** The fixed leg of an interest rate swap: notional in the leg's currency and the annual fixed rate. */
public record FixedLeg(String tradeId, String currency, BigDecimal notional, BigDecimal fixedRate) {

    public FixedLeg {
        Objects.requireNonNull(tradeId, "tradeId");
        Objects.requireNonNull(currency, "currency");
        Objects.requireNonNull(notional, "notional");
        Objects.requireNonNull(fixedRate, "fixedRate");
    }
}
