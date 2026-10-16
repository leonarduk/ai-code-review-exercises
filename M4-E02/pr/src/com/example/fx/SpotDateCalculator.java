package com.example.fx;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Map;

/** Trade date and spot value date for FX deals (FXO-318). */
public final class SpotDateCalculator {

    private static final LocalTime FX_DAY_ROLL = LocalTime.of(17, 0);
    private static final int SPOT_LAG = 2;

    private final Map<String, HolidayCalendar> calendars;

    public SpotDateCalculator(Map<String, HolidayCalendar> calendars) {
        this.calendars = Map.copyOf(calendars);
    }

    /** The FX trade date: the day rolls at 17:00 New York time, and never lands on a weekend. */
    public LocalDate tradeDate(ZonedDateTime executedAt) {
        LocalDateTime local = executedAt.toLocalDateTime();
        LocalDate day = local.toLocalTime().isBefore(FX_DAY_ROLL)
                ? local.toLocalDate()
                : local.toLocalDate().plusDays(1);
        while (day.getDayOfWeek() == DayOfWeek.SATURDAY || day.getDayOfWeek() == DayOfWeek.SUNDAY) {
            day = day.plusDays(1);
        }
        return day;
    }

    /** Trade date plus two business days. */
    public LocalDate spotDate(CurrencyPair pair, ZonedDateTime executedAt) {
        requireCalendars(pair);
        LocalDate date = tradeDate(executedAt);
        int added = 0;
        while (added < SPOT_LAG) {
            date = date.plusDays(1);
            if (calendarFor(pair.base()).isBusinessDay(date)) {
                added++;
            }
        }
        return date;
    }

    private void requireCalendars(CurrencyPair pair) {
        calendarFor(pair.base());
        calendarFor(pair.quote());
    }

    private HolidayCalendar calendarFor(String currency) {
        HolidayCalendar calendar = calendars.get(currency);
        if (calendar == null) {
            throw new IllegalArgumentException("no settlement calendar for " + currency);
        }
        return calendar;
    }
}
