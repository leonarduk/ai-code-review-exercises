package com.example.callbacks;

import java.util.HashMap;
import java.util.Map;

/** Applies each bank callback once, however many times the bank retries it (OPS-2803). */
public final class CallbackInbox {

    public enum Outcome {
        ACCEPTED,
        DUPLICATE,
        CONFLICT
    }

    private final PaymentOutcomes outcomes;
    private final Map<String, BankCallback> byId = new HashMap<>();

    public CallbackInbox(PaymentOutcomes outcomes) {
        this.outcomes = outcomes;
    }

    public Outcome receive(BankCallback callback) {
        BankCallback first = byId.get(callback.callbackId());
        if (first != null) {
            return first.equals(callback) ? Outcome.DUPLICATE : Outcome.CONFLICT;
        }
        outcomes.record(callback.paymentId(), callback.status(), callback.amount());
        byId.put(callback.callbackId(), callback);
        return Outcome.ACCEPTED;
    }
}
