package com.example.futures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;

class ImmDatesTest {

    @Test
    void thirdWednesdays() {
        assertEquals(LocalDate.of(2026, 3, 18), ImmDates.immDate(YearMonth.of(2026, 3)));
        assertEquals(LocalDate.of(2026, 6, 17), ImmDates.immDate(YearMonth.of(2026, 6)));
        assertEquals(LocalDate.of(2026, 9, 16), ImmDates.immDate(YearMonth.of(2026, 9)));
        assertEquals(LocalDate.of(2026, 12, 16), ImmDates.immDate(YearMonth.of(2026, 12)));
    }

    @Test
    void nextImmDateFromMidQuarter() {
        assertEquals(LocalDate.of(2026, 6, 17), ImmDates.nextImmDate(LocalDate.of(2026, 4, 20)));
    }

    @Test
    void frontContract() {
        FuturesContract front = ImmDates.frontContract("SR3", LocalDate.of(2026, 10, 1));
        assertEquals("Z6", front.code());
        assertEquals(LocalDate.of(2026, 12, 16), front.settlementDate());
    }

    @Test
    void onlyQuarterlyMonthsHaveImmDates() {
        assertThrows(IllegalArgumentException.class, () -> ImmDates.immDate(YearMonth.of(2026, 5)));
    }
}
