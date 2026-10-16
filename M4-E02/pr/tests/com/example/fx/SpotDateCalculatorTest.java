package com.example.fx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SpotDateCalculatorTest {

    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
    private static final HolidayCalendar EUR = new HolidayCalendar(
            Set.of(LocalDate.of(2026, 4, 3), LocalDate.of(2026, 4, 6), LocalDate.of(2026, 5, 1)));
    private static final HolidayCalendar USD = new HolidayCalendar(Set.of(LocalDate.of(2026, 7, 3)));

    private final SpotDateCalculator calculator = new SpotDateCalculator(Map.of("EUR", EUR, "USD", USD));

    @Test
    void spotIsTwoBusinessDaysAfterTheTradeDate() {
        ZonedDateTime tuesdayMorning = ZonedDateTime.of(2026, 3, 10, 9, 0, 0, 0, NEW_YORK);
        assertEquals(LocalDate.of(2026, 3, 12), calculator.spotDate(CurrencyPair.parse("EURUSD"), tuesdayMorning));
    }

    @Test
    void tradesAfterFiveInTheAfternoonBelongToTheNextDay() {
        ZonedDateTime tuesdayEvening = ZonedDateTime.of(2026, 3, 10, 17, 30, 0, 0, NEW_YORK);
        assertEquals(LocalDate.of(2026, 3, 11), calculator.tradeDate(tuesdayEvening));
        assertEquals(LocalDate.of(2026, 3, 13), calculator.spotDate(CurrencyPair.parse("EUR/USD"), tuesdayEvening));
    }

    @Test
    void easterHolidaysAreSkipped() {
        ZonedDateTime thursday = ZonedDateTime.of(2026, 4, 2, 11, 0, 0, 0, NEW_YORK);
        assertEquals(LocalDate.of(2026, 4, 8), calculator.spotDate(CurrencyPair.parse("EURUSD"), thursday));
    }

    @Test
    void unknownCurrencyIsRejected() {
        ZonedDateTime monday = ZonedDateTime.of(2026, 3, 9, 9, 0, 0, 0, NEW_YORK);
        assertThrows(IllegalArgumentException.class,
                () -> calculator.spotDate(CurrencyPair.parse("EURXYZ"), monday));
    }
}
