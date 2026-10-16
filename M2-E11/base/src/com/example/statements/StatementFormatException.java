package com.example.statements;

/** A statement line that cannot be imported; the whole file is rejected. */
public class StatementFormatException extends RuntimeException {

    public StatementFormatException(String message) {
        super(message);
    }

    public StatementFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
