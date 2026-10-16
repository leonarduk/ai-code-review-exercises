package com.example.api;

/** POST /payments. */
public final class PaymentHandler {

    private final PaymentGateway gateway;

    public PaymentHandler(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public PaymentResponse handle(PaymentRequest request) {
        return new PaymentResponse(gateway.submit(request), "ACCEPTED");
    }
}
