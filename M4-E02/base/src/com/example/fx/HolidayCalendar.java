package com.example.fx;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

/** A currency's settlement calendar: weekends plus published holidays. */
public final class HolidayCalendar {

    private final Set<LocalDate> holidays;

    public HolidayCalendar(Set<LocalDate> holidays) {
        this.holidays = Set.copyOf(holidays);
    }

    public boolean isBusinessDay(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY && !holidays.contains(date);
    }
}
