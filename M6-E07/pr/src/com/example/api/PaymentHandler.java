package com.example.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** POST /payments, idempotent on the Idempotency-Key header (API-388). */
public final class PaymentHandler {

    /** What was submitted under a key, and what the client was told. */
    record StoredPayment(PaymentRequest request, PaymentResponse response) {
    }

    private final PaymentGateway gateway;
    private final Map<String, StoredPayment> byKey = new HashMap<>();

    public PaymentHandler(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public PaymentResponse handle(String idempotencyKey, PaymentRequest request) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("an Idempotency-Key header is required");
        }
        StoredPayment stored = Optional.ofNullable(byKey.get(idempotencyKey))
                .orElseGet(() -> submit(idempotencyKey, request));
        if (!sameRequest(stored.request(), request)) {
            throw new IdempotencyConflictException(idempotencyKey);
        }
        return stored.response();
    }

    private StoredPayment submit(String idempotencyKey, PaymentRequest request) {
        PaymentResponse response = new PaymentResponse(gateway.submit(request), "ACCEPTED");
        StoredPayment stored = new StoredPayment(request, response);
        byKey.put(idempotencyKey, stored);
        return stored;
    }

    /** Same payment: same accounts, same currency and the same amount by value. */
    static boolean sameRequest(PaymentRequest a, PaymentRequest b) {
        return a.debtorAccount().equals(b.debtorAccount())
                && a.creditorIban().equals(b.creditorIban())
                && a.currency().equals(b.currency())
                && a.amount().compareTo(b.amount()) == 0;
    }
}
