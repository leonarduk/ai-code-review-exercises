package com.example.confirms;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

/** A booked trade as the blotter holds it; executedAt carries the venue's offset. */
public record Trade(String tradeId, LocalDate tradeDate, OffsetDateTime executedAt,
                    BigDecimal quantity, BigDecimal price) {

    public Trade {
        Objects.requireNonNull(tradeId, "tradeId");
        Objects.requireNonNull(tradeDate, "tradeDate");
        Objects.requireNonNull(executedAt, "executedAt");
        Objects.requireNonNull(quantity, "quantity");
        Objects.requireNonNull(price, "price");
    }
}
