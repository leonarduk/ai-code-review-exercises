package com.example.reporting;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * One fill as captured by the order-management system. executedAt carries
 * the venue's own time zone (Europe/London for XLON, America/New_York for
 * XNYS, and so on).
 */
public record Execution(String transactionId, String isin, BigDecimal quantity, BigDecimal price,
        ZonedDateTime executedAt) {

    public Execution {
        Objects.requireNonNull(transactionId, "transactionId");
        Objects.requireNonNull(isin, "isin");
        Objects.requireNonNull(quantity, "quantity");
        Objects.requireNonNull(price, "price");
        Objects.requireNonNull(executedAt, "executedAt");
    }
}
