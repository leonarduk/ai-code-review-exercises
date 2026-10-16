package com.example.api;

/** Submits a payment to the payment engine and returns its payment id; throws if the engine refuses it. */
@FunctionalInterface
public interface PaymentGateway {

    String submit(PaymentRequest request);
}
