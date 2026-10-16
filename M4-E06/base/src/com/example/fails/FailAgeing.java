package com.example.fails;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/** Ages settlement fails for the daily fails report. */
public final class FailAgeing {

    private final Clock clock;

    public FailAgeing(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    /** Calendar days since the intended settlement date. */
    public long daysOutstanding(LocalDate intendedSettlementDate) {
        return Math.max(0, ChronoUnit.DAYS.between(intendedSettlementDate, LocalDate.now(clock)));
    }

    public Bucket bucket(LocalDate intendedSettlementDate) {
        long days = daysOutstanding(intendedSettlementDate);
        if (days >= 21) {
            return Bucket.BUY_IN;
        }
        if (days >= 7) {
            return Bucket.ESCALATE;
        }
        return days > 0 ? Bucket.MONITOR : Bucket.CURRENT;
    }
}
