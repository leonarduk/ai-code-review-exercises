package com.example.invoices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Statutory interest on overdue invoices: simple interest, ACT/365 Fixed,
 * from the due date up to (not including) the payment date, rounded half-up
 * to the cent.
 */
public final class LatePaymentInterest {

    private static final BigDecimal DAYS_IN_YEAR = BigDecimal.valueOf(365);
    private static final BigDecimal NO_INTEREST = BigDecimal.ZERO.setScale(2);

    private final BigDecimal annualRate;

    public LatePaymentInterest(StatutoryRates rates) {
        Objects.requireNonNull(rates, "rates");
        this.annualRate = BigDecimal.valueOf(rates.latePaymentAnnualRate());
    }

    public BigDecimal interest(BigDecimal overdueAmount, LocalDate dueDate, LocalDate paidDate) {
        Objects.requireNonNull(overdueAmount, "overdueAmount");
        if (overdueAmount.compareTo(BigDecimal.ZERO) <= 0 || !paidDate.isAfter(dueDate)) {
            return NO_INTEREST;
        }
        long daysLate = ChronoUnit.DAYS.between(dueDate, paidDate);
        return overdueAmount
                .multiply(annualRate)
                .multiply(BigDecimal.valueOf(daysLate))
                .divide(DAYS_IN_YEAR, 2, RoundingMode.HALF_UP);
    }
}
