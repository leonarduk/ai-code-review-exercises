package com.example.options;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;

/** When an FX option expires, and how long it has left (OPT-772). */
public record OptionExpiry(LocalDate expiryDate, Cut cut) {

    private static final double SECONDS_PER_YEAR = 365.0 * 24 * 60 * 60;

    public OptionExpiry {
        Objects.requireNonNull(expiryDate, "expiryDate");
        Objects.requireNonNull(cut, "cut");
    }

    /** The cut time on the expiry date, in the cut's own market. */
    public Instant instant() {
        return ZonedDateTime.of(expiryDate, cut.time(), cut.zone()).toInstant();
    }

    /** True from the moment of the cut onwards. */
    public boolean isExpired(Clock clock) {
        return !clock.instant().isBefore(instant());
    }

    /** Elapsed time to the cut as a fraction of a 365-day year; zero once expired. */
    public double yearsToExpiry(Clock clock) {
        long seconds = Duration.between(clock.instant(), instant()).getSeconds();
        return Math.max(0, seconds) / SECONDS_PER_YEAR;
    }
}
