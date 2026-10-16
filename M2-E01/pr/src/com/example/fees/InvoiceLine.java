package com.example.fees;

import java.math.BigDecimal;

public record InvoiceLine(String accountId, String description, BigDecimal amount) {
}
