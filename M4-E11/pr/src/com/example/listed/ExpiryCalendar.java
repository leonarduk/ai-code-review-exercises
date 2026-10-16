package com.example.listed;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Expiry dates and listed series for monthly index options (LST-233). */
public final class ExpiryCalendar {

    private static final DateTimeFormatter CODE = DateTimeFormatter.ofPattern("MMMuu", Locale.ENGLISH);
    private static final int LISTED_MONTHS = 3;

    private final Set<LocalDate> holidays;

    public ExpiryCalendar(Collection<LocalDate> holidays) {
        this.holidays = Set.copyOf(holidays);
    }

    /** Last Thursday of the month, or the trading day before it when that Thursday is not a trading day. */
    public LocalDate expiry(YearMonth month) {
        LocalDate day = month.atDay(1).with(TemporalAdjusters.lastInMonth(DayOfWeek.THURSDAY));
        while (!isTradingDay(day)) {
            day = day.minusDays(1);
        }
        return day;
    }

    public boolean isTradingDay(LocalDate date) {
        return date.getDayOfWeek().getValue() <= 5 && !holidays.contains(date);
    }

    /** APR26: the English month abbreviation in capitals and the two-digit year. */
    public static String code(YearMonth month) {
        return month.format(CODE).toUpperCase(Locale.ROOT);
    }

    /** The series listed on a date: the month of the next expiry on or after it, and the two after. */
    public List<IndexOption> listed(String index, LocalDate date) {
        YearMonth front = YearMonth.from(date);
        if (expiry(front).isBefore(date)) {
            front = front.plusMonths(1);
        }
        List<IndexOption> series = new ArrayList<>();
        for (int i = 0; i < LISTED_MONTHS; i++) {
            YearMonth month = front.plusMonths(i);
            series.add(new IndexOption(index, code(month), expiry(month)));
        }
        return List.copyOf(series);
    }
}
