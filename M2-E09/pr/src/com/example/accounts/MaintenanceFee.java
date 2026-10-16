package com.example.accounts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/** The monthly maintenance fee on current accounts. */
public final class MaintenanceFee {

    public static final BigDecimal MONTHLY_FEE = new BigDecimal("12.00");
    public static final BigDecimal WAIVER_THRESHOLD = new BigDecimal("5000.00");
    private static final BigDecimal NO_FEE = new BigDecimal("0.00");
    private static final int CENT_SCALE = 2;

    private MaintenanceFee() {
    }

    /**
     * The average daily balance for a statement period, given one
     * end-of-day balance per calendar day, rounded half-even to the cent.
     * This is the figure printed on the statement.
     */
    public static BigDecimal averageDailyBalance(List<BigDecimal> endOfDayBalances) {
        Objects.requireNonNull(endOfDayBalances, "endOfDayBalances");
        if (endOfDayBalances.isEmpty()) {
            throw new IllegalArgumentException("no balances for the period");
        }
        BigDecimal total = endOfDayBalances.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal days = BigDecimal.valueOf(endOfDayBalances.size());
        return total.divide(days).setScale(CENT_SCALE, RoundingMode.HALF_EVEN);
    }

    /**
     * The fee for a statement period. Waived when the average daily
     * balance, as printed on the statement, is at least the threshold.
     */
    public static BigDecimal forPeriod(List<BigDecimal> endOfDayBalances) {
        BigDecimal average = averageDailyBalance(endOfDayBalances);
        return average.compareTo(WAIVER_THRESHOLD) >= 0 ? NO_FEE : MONTHLY_FEE;
    }
}
