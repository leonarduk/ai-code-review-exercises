package com.example.clearing;

/** A sterling payment in a clearing submission, with its amount in pence. */
public record Payment(String reference, String sortCode, String accountNumber, long amountPence) {
}
