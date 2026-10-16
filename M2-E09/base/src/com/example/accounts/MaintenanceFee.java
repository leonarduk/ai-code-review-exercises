package com.example.accounts;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/** The monthly maintenance fee on current accounts. */
public final class MaintenanceFee {

    public static final BigDecimal MONTHLY_FEE = new BigDecimal("12.00");
    public static final BigDecimal WAIVER_THRESHOLD = new BigDecimal("5000.00");
    private static final BigDecimal NO_FEE = new BigDecimal("0.00");

    private MaintenanceFee() {
    }

    /**
     * The fee for a statement period, given the account's end-of-day
     * balances in date order. Waived when the closing balance is at least
     * the threshold.
     */
    public static BigDecimal forPeriod(List<BigDecimal> endOfDayBalances) {
        Objects.requireNonNull(endOfDayBalances, "endOfDayBalances");
        if (endOfDayBalances.isEmpty()) {
            throw new IllegalArgumentException("no balances for the period");
        }
        BigDecimal closing = endOfDayBalances.get(endOfDayBalances.size() - 1);
        return closing.compareTo(WAIVER_THRESHOLD) >= 0 ? NO_FEE : MONTHLY_FEE;
    }
}
