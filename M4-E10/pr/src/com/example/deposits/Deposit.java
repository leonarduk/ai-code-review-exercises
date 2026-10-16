package com.example.deposits;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/** A money-market deposit as the treasury system holds it. */
public record Deposit(String dealRef, String currency, BigDecimal principal, LocalDate startDate,
                      LocalDate maturityDate, String tenor, Instant bookedAt) {

    public Deposit {
        Objects.requireNonNull(dealRef, "dealRef");
        Objects.requireNonNull(currency, "currency");
        Objects.requireNonNull(principal, "principal");
        Objects.requireNonNull(startDate, "startDate");
        Objects.requireNonNull(maturityDate, "maturityDate");
        Objects.requireNonNull(tenor, "tenor");
        Objects.requireNonNull(bookedAt, "bookedAt");
        if (!maturityDate.isAfter(startDate)) {
            throw new IllegalArgumentException(dealRef + ": maturity " + maturityDate + " is not after start " + startDate);
        }
    }
}
