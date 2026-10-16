package com.example.financing;

import java.math.BigDecimal;
import java.util.Objects;

/** A client's leveraged position, financed overnight in its own currency. */
public record Position(String id, String currency, BigDecimal notional) {

    public Position {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(currency, "currency");
        Objects.requireNonNull(notional, "notional");
        if (notional.signum() <= 0) {
            throw new IllegalArgumentException("notional must be positive");
        }
    }
}
