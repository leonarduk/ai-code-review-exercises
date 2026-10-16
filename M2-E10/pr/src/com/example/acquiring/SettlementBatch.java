package com.example.acquiring;

import java.util.List;
import java.util.Objects;

/**
 * One merchant's daily settlement batch, in a single currency's minor
 * units. Totals are exact: an overflow is an ArithmeticException, never a
 * wrapped value.
 */
public final class SettlementBatch {

    /** The highest merchant discount rate the acquirer agrees: 10%. */
    public static final int MAX_MDR_BASIS_POINTS = 1_000;
    private static final double BASIS_POINTS_PER_UNIT = 10_000.0;

    private final List<Transaction> transactions;
    private final int mdrBasisPoints;

    public SettlementBatch(List<Transaction> transactions, int mdrBasisPoints) {
        this.transactions = List.copyOf(Objects.requireNonNull(transactions, "transactions"));
        if (mdrBasisPoints < 0 || mdrBasisPoints > MAX_MDR_BASIS_POINTS) {
            throw new IllegalArgumentException("MDR out of range: " + mdrBasisPoints + " bp");
        }
        this.mdrBasisPoints = mdrBasisPoints;
    }

    /** Sales minus refunds. */
    public long grossMinor() {
        long gross = 0;
        for (Transaction t : transactions) {
            gross = Math.addExact(gross, t.amountMinor());
        }
        return gross;
    }

    /**
     * The merchant discount on one transaction, rounded half away from zero
     * to the minor unit. A refund returns the fee charged on the sale.
     */
    public long feeMinor(Transaction t) {
        long scaled = t.amountMinor() * mdrBasisPoints;
        return Math.round(scaled / BASIS_POINTS_PER_UNIT);
    }

    /** The batch's merchant discount: the sum of the transaction fees. */
    public long feesMinor() {
        var fees = 0;
        for (Transaction t : transactions) {
            fees += feeMinor(t);
        }
        return fees;
    }

    /** What the merchant is paid: gross minus fees. */
    public long netMinor() {
        return Math.subtractExact(grossMinor(), feesMinor());
    }
}
