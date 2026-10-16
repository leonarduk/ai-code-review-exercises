package com.example.futures;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

/** IMM dates and contract codes for quarterly interest-rate futures (FUT-509). */
public final class ImmDates {

    private static final Map<Month, Character> MONTH_CODES =
            Map.of(Month.MARCH, 'H', Month.JUNE, 'M', Month.SEPTEMBER, 'U', Month.DECEMBER, 'Z');

    private ImmDates() {
    }

    /** The IMM date of a quarterly month: its third Wednesday. */
    public static LocalDate immDate(YearMonth month) {
        if (!MONTH_CODES.containsKey(month.getMonth())) {
            throw new IllegalArgumentException("not a quarterly contract month: " + month);
        }
        return month.atDay(1).with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY)).plusWeeks(2);
    }

    /** The first IMM date on or after the given date. */
    public static LocalDate nextImmDate(LocalDate date) {
        YearMonth month = YearMonth.from(date);
        while (true) {
            if (MONTH_CODES.containsKey(month.getMonth())) {
                LocalDate imm = immDate(month);
                if (!imm.isBefore(date)) {
                    return imm;
                }
            }
            month = month.plusMonths(1);
        }
    }

    /** The front quarterly contract on a date: the one whose IMM date is next. */
    public static FuturesContract frontContract(String root, LocalDate date) {
        LocalDate imm = nextImmDate(date);
        String code = "" + MONTH_CODES.get(imm.getMonth()) + imm.getYear() % 10;
        return new FuturesContract(root, code, imm);
    }
}
