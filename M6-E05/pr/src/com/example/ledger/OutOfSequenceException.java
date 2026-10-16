package com.example.ledger;

/** An event arrived before the events ahead of it for the same account. */
public final class OutOfSequenceException extends RuntimeException {

    public OutOfSequenceException(String accountId, long expected, long received) {
        super("account " + accountId + ": expected sequence " + expected + ", received " + received);
    }
}
