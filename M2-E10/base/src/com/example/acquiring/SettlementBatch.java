package com.example.acquiring;

import java.util.List;
import java.util.Objects;

/**
 * One merchant's daily settlement batch, in a single currency's minor
 * units. Totals are exact: an overflow is an ArithmeticException, never a
 * wrapped value.
 */
public final class SettlementBatch {

    private final List<Transaction> transactions;

    public SettlementBatch(List<Transaction> transactions) {
        this.transactions = List.copyOf(Objects.requireNonNull(transactions, "transactions"));
    }

    /** Sales minus refunds. */
    public long grossMinor() {
        long gross = 0;
        for (Transaction t : transactions) {
            gross = Math.addExact(gross, t.amountMinor());
        }
        return gross;
    }
}
