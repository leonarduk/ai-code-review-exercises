package com.example.fails;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class FailAgeingTest {

    private static final List<String> HOLIDAYS = List.of("# London 2026", "2026-04-03", "2026-04-06", "", "2026-05-04");

    private static FailAgeing at(String instant) {
        return new FailAgeing(Clock.fixed(Instant.parse(instant), ZoneOffset.UTC), HOLIDAYS);
    }

    @Test
    void settlingTodayIsCurrent() {
        FailAgeing ageing = at("2026-06-10T12:00:00Z");
        assertEquals(0, ageing.businessDaysOutstanding(LocalDate.of(2026, 6, 10)));
        assertEquals(Bucket.CURRENT, ageing.bucket(LocalDate.of(2026, 6, 10)));
    }

    @Test
    void weekendsDoNotCount() {
        FailAgeing ageing = at("2026-06-15T12:00:00Z");
        assertEquals(1, ageing.businessDaysOutstanding(LocalDate.of(2026, 6, 12)));
        assertEquals(Bucket.MONITOR, ageing.bucket(LocalDate.of(2026, 6, 12)));
    }

    @Test
    void easterHolidaysDoNotCount() {
        FailAgeing ageing = at("2026-04-10T12:00:00Z");
        assertEquals(5, ageing.businessDaysOutstanding(LocalDate.of(2026, 4, 1)));
        assertEquals(Bucket.ESCALATE, ageing.bucket(LocalDate.of(2026, 4, 1)));
    }

    @Test
    void threeWeeksOutstandingIsABuyIn() {
        assertEquals(Bucket.BUY_IN, at("2026-07-01T12:00:00Z").bucket(LocalDate.of(2026, 6, 10)));
    }
}
