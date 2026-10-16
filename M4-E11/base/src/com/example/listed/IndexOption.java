package com.example.listed;

import java.time.LocalDate;
import java.util.Objects;

/** A listed monthly index option series: underlying index, contract code and expiry date. */
public record IndexOption(String index, String code, LocalDate expiry) {

    public IndexOption {
        Objects.requireNonNull(index, "index");
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(expiry, "expiry");
    }
}
