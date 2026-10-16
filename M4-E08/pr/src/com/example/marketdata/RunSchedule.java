package com.example.marketdata;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/** When a capture job next runs: a local time of day in a market's zone (MKT-877). */
public record RunSchedule(LocalTime runTime, ZoneId zone) {

    public RunSchedule {
        Objects.requireNonNull(runTime, "runTime");
        Objects.requireNonNull(zone, "zone");
    }

    /** The first daily run strictly after the given instant. */
    public Instant nextDailyRun(Instant after) {
        Instant today = runOn(LocalDate.ofInstant(after, zone));
        return today.isAfter(after) ? today : today.plus(1, ChronoUnit.DAYS);
    }

    /** The first run on the 1st of a month strictly after the given instant. */
    public Instant nextMonthlyRun(Instant after) {
        Instant thisMonth = runOn(LocalDate.ofInstant(after, zone).withDayOfMonth(1));
        return thisMonth.isAfter(after) ? thisMonth : thisMonth.plus(1, ChronoUnit.MONTHS);
    }

    private Instant runOn(LocalDate date) {
        return ZonedDateTime.of(date, runTime, zone).toInstant();
    }
}
