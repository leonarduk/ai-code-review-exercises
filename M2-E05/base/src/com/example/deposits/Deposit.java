package com.example.deposits;

import java.math.BigDecimal;
import java.time.LocalDate;

/** A money-market deposit booked by Treasury. */
public record Deposit(BigDecimal principal, BigDecimal annualRate, LocalDate valueDate, LocalDate maturityDate) {
}
