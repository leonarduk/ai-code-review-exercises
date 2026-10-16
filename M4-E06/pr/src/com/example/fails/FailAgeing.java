package com.example.fails;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/** Ages settlement fails for the daily fails report, in London business days (OPS-2318). */
public final class FailAgeing {

    private static final ZoneId LONDON = ZoneId.of("Europe/London");
    private static final int ESCALATE_FROM = 5;
    private static final int BUY_IN_FROM = 15;

    private final Clock clock;
    private final Set<LocalDate> holidays;

    /** holidayLines: the London holiday file, one ISO date per line; blank lines and # comments are skipped. */
    public FailAgeing(Clock clock, Collection<String> holidayLines) {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.holidays = holidayLines.stream()
                .map(String::strip)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .map(LocalDate::parse)
                .collect(Collectors.toUnmodifiableSet());
    }

    /** Today's date in London, wherever this runs. */
    public LocalDate today() {
        return LocalDate.ofInstant(clock.instant(), LONDON);
    }

    /** London business days after the intended settlement date, up to and including today. */
    public int businessDaysOutstanding(LocalDate intendedSettlementDate) {
        int days = 0;
        for (LocalDate d = intendedSettlementDate.plusDays(1); !d.isAfter(today()); d = d.plusDays(1)) {
            if (isBusinessDay(d)) {
                days++;
            }
        }
        return days;
    }

    public Bucket bucket(LocalDate intendedSettlementDate) {
        int days = businessDaysOutstanding(intendedSettlementDate);
        if (days >= BUY_IN_FROM) {
            return Bucket.BUY_IN;
        }
        if (days >= ESCALATE_FROM) {
            return Bucket.ESCALATE;
        }
        return days > 0 ? Bucket.MONITOR : Bucket.CURRENT;
    }

    private boolean isBusinessDay(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY && !holidays.contains(date);
    }
}
