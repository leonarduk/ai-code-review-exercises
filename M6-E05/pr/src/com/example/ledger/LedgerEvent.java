package com.example.ledger;

import java.math.BigDecimal;
import java.util.Objects;

/** A posting event from the payments topic. Sequence numbers are per account and start at 1. */
public record LedgerEvent(String eventId, String accountId, Long sequence, BigDecimal amount) {

    public LedgerEvent {
        Objects.requireNonNull(eventId, "eventId");
        Objects.requireNonNull(accountId, "accountId");
        Objects.requireNonNull(sequence, "sequence");
        Objects.requireNonNull(amount, "amount");
    }
}
