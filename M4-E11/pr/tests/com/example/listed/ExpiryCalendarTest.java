package com.example.listed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ExpiryCalendarTest {

    private static final ExpiryCalendar CALENDAR = new ExpiryCalendar(Set.of(LocalDate.of(2026, 5, 28)));

    @Test
    void lastThursdayOfTheMonth() {
        assertEquals(LocalDate.of(2026, 6, 25), CALENDAR.expiry(YearMonth.of(2026, 6)));
        assertEquals(LocalDate.of(2026, 3, 26), CALENDAR.expiry(YearMonth.of(2026, 3)));
    }

    @Test
    void aHolidayThursdayMovesExpiryToTheDayBefore() {
        assertEquals(LocalDate.of(2026, 5, 27), CALENDAR.expiry(YearMonth.of(2026, 5)));
    }

    @Test
    void tradingDays() {
        assertTrue(CALENDAR.isTradingDay(LocalDate.of(2026, 6, 15)));
        assertFalse(CALENDAR.isTradingDay(LocalDate.of(2026, 6, 13)));
        assertFalse(CALENDAR.isTradingDay(LocalDate.of(2026, 5, 28)));
    }

    @Test
    void contractCode() {
        assertEquals("JUN26", ExpiryCalendar.code(YearMonth.of(2026, 6)));
    }

    @Test
    void listedSeriesMidMonth() {
        List<IndexOption> listed = CALENDAR.listed("UKX", LocalDate.of(2026, 6, 10));
        assertEquals(List.of(
                new IndexOption("UKX", "JUN26", LocalDate.of(2026, 6, 25)),
                new IndexOption("UKX", "JUL26", LocalDate.of(2026, 7, 30)),
                new IndexOption("UKX", "AUG26", LocalDate.of(2026, 8, 27))), listed);
    }
}
