package com.example.payments;

import java.math.BigDecimal;
import java.time.LocalDate;

/** A payment instruction as received from one of the intake channels (file, API, branch). */
public record PaymentInstruction(String instructionId, String channel, String debtorIban, String creditorIban,
        BigDecimal amount, String currency, LocalDate executionDate) {
}
