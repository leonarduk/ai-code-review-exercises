package com.example.financing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Map;

/** Overnight financing on leveraged positions (FIN-1184). */
public final class FinancingCharge {

    private static final ZoneId LONDON = ZoneId.of("Europe/London");
    private static final Map<String, Integer> DAY_COUNT_BASIS =
            Map.of("GBP", 365, "JPY", 365, "USD", 360, "EUR", 360, "CHF", 360);

    private FinancingCharge() {
    }

    /** Nights the position is held: one per London calendar day from the open date to the close date. */
    public static long nights(LocalDate openDate, LocalDate closeDate) {
        if (closeDate.isBefore(openDate)) {
            throw new IllegalArgumentException("closed before it was opened");
        }
        return Duration.between(openDate.atStartOfDay(LONDON), closeDate.atStartOfDay(LONDON)).toDays();
    }

    /** notional x annual rate x nights / basis, rounded half-even to the currency's minor unit. */
    public static BigDecimal charge(Position position, BigDecimal annualRate, LocalDate openDate,
            LocalDate closeDate) {
        Integer basis = DAY_COUNT_BASIS.get(position.currency());
        if (basis == null) {
            throw new IllegalArgumentException("no day count basis for " + position.currency());
        }
        int minorUnits = Currency.getInstance(position.currency()).getDefaultFractionDigits();
        return position.notional()
                .multiply(annualRate)
                .multiply(BigDecimal.valueOf(nights(openDate, closeDate)))
                .divide(BigDecimal.valueOf(basis), minorUnits, RoundingMode.HALF_EVEN);
    }
}
