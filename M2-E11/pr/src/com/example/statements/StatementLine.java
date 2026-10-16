package com.example.statements;

import java.math.BigDecimal;
import java.time.LocalDate;

/** One line of a bank statement: negative amounts are debits. */
public record StatementLine(LocalDate date, String description, BigDecimal amount) {
}
