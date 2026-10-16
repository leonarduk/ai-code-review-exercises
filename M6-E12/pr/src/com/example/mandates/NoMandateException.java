package com.example.mandates;

/** A collection was presented with no active mandate behind it; the bank returns it with reason MD01. */
public class NoMandateException extends RuntimeException {

    public NoMandateException(String creditorId, String reference) {
        super("no active mandate " + reference + " for creditor " + creditorId);
    }
}
