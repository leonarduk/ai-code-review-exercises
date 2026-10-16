package com.example.futures;

import java.time.LocalDate;
import java.util.Objects;

/** A listed interest-rate future, identified by its product root and contract code (e.g. SR3 Z7). */
public record FuturesContract(String root, String code, LocalDate settlementDate) {

    public FuturesContract {
        Objects.requireNonNull(root, "root");
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(settlementDate, "settlementDate");
    }
}
