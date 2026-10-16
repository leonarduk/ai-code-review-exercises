package com.example.marketdata;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class RunScheduleTest {

    private static final RunSchedule FOUR_PM_LONDON = new RunSchedule(LocalTime.of(16, 0), ZoneId.of("Europe/London"));

    @Test
    void laterTheSameDay() {
        assertEquals(Instant.parse("2026-01-14T16:00:00Z"),
                FOUR_PM_LONDON.nextDailyRun(Instant.parse("2026-01-14T09:00:00Z")));
    }

    @Test
    void afterTodaysRunItIsTomorrow() {
        assertEquals(Instant.parse("2026-01-15T16:00:00Z"),
                FOUR_PM_LONDON.nextDailyRun(Instant.parse("2026-01-14T16:00:00Z")));
    }

    @Test
    void summerTime() {
        assertEquals(Instant.parse("2026-07-14T15:00:00Z"),
                FOUR_PM_LONDON.nextDailyRun(Instant.parse("2026-07-14T08:00:00Z")));
    }

    @Test
    void monthlyRunOnTheFirst() {
        assertEquals(Instant.parse("2026-06-01T15:00:00Z"),
                FOUR_PM_LONDON.nextMonthlyRun(Instant.parse("2026-05-31T23:30:00Z")));
    }
}
