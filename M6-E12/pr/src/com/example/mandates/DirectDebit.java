package com.example.mandates;

import java.math.BigDecimal;

/** A collection accepted under a mandate. */
public record DirectDebit(Mandate mandate, BigDecimal amount, SequenceType sequenceType) {

    /** FRST for the first collection under a mandate, RCUR for every later one. */
    public enum SequenceType {
        FRST,
        RCUR
    }
}
