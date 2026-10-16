package com.example.ledger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Account balances. Posting to an account that is not open fails. */
public final class Ledger {

    private final Map<String, BigDecimal> balances = new HashMap<>();
    private final Set<String> open = new HashSet<>();

    public void open(String accountId) {
        open.add(accountId);
        balances.putIfAbsent(accountId, new BigDecimal("0.00"));
    }

    public void close(String accountId) {
        open.remove(accountId);
    }

    public void post(String accountId, BigDecimal amount) {
        if (!open.contains(accountId)) {
            throw new IllegalStateException("account " + accountId + " is not open");
        }
        balances.merge(accountId, amount, BigDecimal::add);
    }

    public BigDecimal balance(String accountId) {
        return balances.getOrDefault(accountId, new BigDecimal("0.00"));
    }
}
