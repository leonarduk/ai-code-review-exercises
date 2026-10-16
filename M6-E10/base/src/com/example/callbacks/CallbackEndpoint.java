package com.example.callbacks;

import java.math.BigDecimal;

/** POST /callbacks/payments. The gateway has already verified the signature. */
public final class CallbackEndpoint {

    private final PaymentOutcomes outcomes;

    public CallbackEndpoint(PaymentOutcomes outcomes) {
        this.outcomes = outcomes;
    }

    /** Records the outcome in the body {@code callbackId|paymentId|status|amount} and returns the HTTP status. */
    public int post(String body, byte[] signature) {
        String[] f = body.split("\\|");
        outcomes.record(f[1], f[2], new BigDecimal(f[3]));
        return 200;
    }
}
