package com.example.swaps;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/** ACT/ACT (ISDA) accrual for fixed swap legs (IRS-1450). */
public final class ActActIsda {

    private static final BigDecimal LEAP_YEAR = BigDecimal.valueOf(366);
    private static final BigDecimal COMMON_YEAR = BigDecimal.valueOf(365);

    private ActActIsda() {
    }

    /** Days in leap years / 366 plus days in other years / 365, start included, end excluded. */
    public static BigDecimal yearFraction(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("period ends " + end + " before it starts " + start);
        }
        BigDecimal fraction = BigDecimal.ZERO;
        LocalDate from = start;
        while (from.isBefore(end)) {
            LocalDate yearEnd = from.with(TemporalAdjusters.lastDayOfYear());
            LocalDate to = end.isBefore(yearEnd) ? end : yearEnd;
            BigDecimal days = BigDecimal.valueOf(ChronoUnit.DAYS.between(from, to));
            BigDecimal basis = Year.isLeap(from.getYear()) ? LEAP_YEAR : COMMON_YEAR;
            fraction = fraction.add(days.divide(basis, MathContext.DECIMAL128));
            from = to.plusDays(1);
        }
        return fraction;
    }

    /** Notional x fixed rate x year fraction, rounded once, half-even, to the cent. */
    public static BigDecimal interest(FixedLeg leg, LocalDate start, LocalDate end) {
        return leg.notional()
                .multiply(leg.fixedRate())
                .multiply(yearFraction(start, end))
                .setScale(2, RoundingMode.HALF_EVEN);
    }
}
