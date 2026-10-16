package com.example.options;

import java.time.LocalTime;
import java.time.ZoneId;

/** The expiry cuts the desk trades: a local time of day in a named market. */
public enum Cut {
    NEW_YORK(LocalTime.of(10, 0), ZoneId.of("America/New_York")),
    TOKYO(LocalTime.of(15, 0), ZoneId.of("Asia/Tokyo")),
    LONDON(LocalTime.of(14, 0), ZoneId.of("Europe/London"));

    private final LocalTime time;
    private final ZoneId zone;

    Cut(LocalTime time, ZoneId zone) {
        this.time = time;
        this.zone = zone;
    }

    public LocalTime time() {
        return time;
    }

    public ZoneId zone() {
        return zone;
    }
}
