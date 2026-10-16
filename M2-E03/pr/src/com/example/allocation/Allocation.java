package com.example.allocation;

import java.math.BigDecimal;

public record Allocation(String accountId, BigDecimal balance, BigDecimal amount) {
}
