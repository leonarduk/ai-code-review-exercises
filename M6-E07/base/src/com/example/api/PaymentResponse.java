package com.example.api;

/** The response to POST /payments. */
public record PaymentResponse(String paymentId, String status) {
}
