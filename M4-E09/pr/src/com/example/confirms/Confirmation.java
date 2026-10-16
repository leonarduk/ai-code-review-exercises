package com.example.confirms;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

/** One line of a counterparty's end-of-day confirmation file. */
public record Confirmation(String tradeId, LocalDate tradeDate, OffsetDateTime executedAt,
                           BigDecimal quantity, BigDecimal price) {

    public Confirmation {
        Objects.requireNonNull(tradeId, "tradeId");
        Objects.requireNonNull(tradeDate, "tradeDate");
        Objects.requireNonNull(executedAt, "executedAt");
        Objects.requireNonNull(quantity, "quantity");
        Objects.requireNonNull(price, "price");
    }
}
