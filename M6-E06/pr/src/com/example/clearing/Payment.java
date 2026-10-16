package com.example.clearing;

import java.math.BigDecimal;

/** A sterling payment in a clearing submission, with its amount in pence. */
public record Payment(String reference, String sortCode, String accountNumber, long amountPence) {

    /** A payment from an amount in pounds; fractions of a penny are rejected. */
    public static Payment ofPounds(String reference, String sortCode, String accountNumber, BigDecimal pounds) {
        long pence;
        try {
            pence = pounds.movePointRight(2).longValueExact();
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(reference + ": " + pounds + " is not a whole number of pence", e);
        }
        return new Payment(reference, sortCode, accountNumber, pence);
    }
}
