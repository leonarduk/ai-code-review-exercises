package com.example.iso20022;

import java.math.BigDecimal;
import java.time.LocalDate;

/** One euro credit transfer as the customer entered it in online banking. */
public record Transfer(String endToEndId, String creditorName, String creditorIban, BigDecimal amount,
        LocalDate executionDate, String remittanceInfo) {
}
