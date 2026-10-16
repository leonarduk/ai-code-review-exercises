package com.example.batch;

import java.math.BigDecimal;
import java.util.Objects;

/** One credit transfer in an outgoing pain.001 batch. */
public record CreditTransfer(String endToEndId, String creditorIban, BigDecimal amount, String currency) {

    public CreditTransfer {
        Objects.requireNonNull(endToEndId, "endToEndId");
        Objects.requireNonNull(creditorIban, "creditorIban");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
    }
}
