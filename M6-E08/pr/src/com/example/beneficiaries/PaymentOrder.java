package com.example.beneficiaries;

import java.math.BigDecimal;

/** An outgoing payment as entered by the customer. */
public record PaymentOrder(String creditorIban, String creditorName, BigDecimal amount) {
}
