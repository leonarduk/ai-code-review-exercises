package com.example.fees;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

/**
 * Monthly management fee, charged in arrears on assets under management.
 *
 * <p>Fee = assets under management x annual rate / 12, rounded to the cent
 * with banker's rounding. No invoice line is raised for a zero fee.
 */
public final class MonthlyFeeCalculator {

    private static final BigDecimal MONTHS_PER_YEAR = BigDecimal.valueOf(12);
    private static final int CENT_SCALE = 2;

    private final BigDecimal annualRate;

    public MonthlyFeeCalculator(FeeSchedule schedule) {
        Objects.requireNonNull(schedule, "schedule");
        this.annualRate = new BigDecimal(schedule.annualRate());
    }

    public BigDecimal monthlyFee(BigDecimal assetsUnderManagement) {
        Objects.requireNonNull(assetsUnderManagement, "assetsUnderManagement");
        if (assetsUnderManagement.signum() < 0) {
            throw new IllegalArgumentException("assets under management cannot be negative");
        }
        return assetsUnderManagement
                .multiply(annualRate)
                .divide(MONTHS_PER_YEAR, CENT_SCALE, RoundingMode.HALF_EVEN);
    }

    public Optional<InvoiceLine> invoiceLine(String accountId, BigDecimal assetsUnderManagement) {
        BigDecimal fee = monthlyFee(assetsUnderManagement);
        if (fee.equals(BigDecimal.ZERO)) {
            return Optional.empty();
        }
        return Optional.of(new InvoiceLine(accountId, "Management fee", fee));
    }
}
