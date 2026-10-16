package com.example.api;

/** An Idempotency-Key reused for a different payment; the API answers 422. */
public final class IdempotencyConflictException extends RuntimeException {

    public IdempotencyConflictException(String idempotencyKey) {
        super("Idempotency-Key " + idempotencyKey + " was already used for a different payment");
    }
}
