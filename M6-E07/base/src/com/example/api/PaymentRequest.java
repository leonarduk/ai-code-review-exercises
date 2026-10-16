package com.example.api;

import java.math.BigDecimal;

/** The body of POST /payments. */
public record PaymentRequest(String debtorAccount, String creditorIban, BigDecimal amount, String currency) {
}
