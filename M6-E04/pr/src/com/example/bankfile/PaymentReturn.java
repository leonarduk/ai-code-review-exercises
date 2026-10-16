package com.example.bankfile;

import java.math.BigDecimal;

/** One line of the bank's return file: the outcome of a payment we sent. */
public record PaymentReturn(String reference, Status status, BigDecimal amount, String reasonCode, String reasonText) {

    public enum Status {
        ACCEPTED,
        REJECTED
    }
}
