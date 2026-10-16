package com.example.settlement;

/** A settlement file that cannot be booked as a whole. */
public class SettlementFileException extends RuntimeException {

    public SettlementFileException(String message) {
        super(message);
    }

    public SettlementFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
