package com.example.deposits;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/** Maps DepositsPlus records to treasury deposits and back (TRY-912). */
public final class LegacyDepositAdapter {

    private LegacyDepositAdapter() {
    }

    public static Deposit fromLegacy(LegacyDepositRecord record) {
        LocalDate start = toLocalDate(record.getStartDate());
        LocalDate maturity = toLocalDate(record.getMaturityDate());
        return new Deposit(record.getDealRef(), record.getCurrencyCode(), record.getPrincipal(),
                start, maturity, tenor(start, maturity), record.getBookedTimestamp().toInstant());
    }

    public static LegacyDepositRecord toLegacy(Deposit deposit) {
        LegacyDepositRecord record = new LegacyDepositRecord();
        record.setDealRef(deposit.dealRef());
        record.setCurrencyCode(deposit.currency());
        record.setPrincipal(deposit.principal());
        record.setStartDate(toLegacyDate(deposit.startDate()));
        record.setMaturityDate(toLegacyDate(deposit.maturityDate()));
        record.setBookedTimestamp(Date.from(deposit.bookedAt()));
        return record;
    }

    /** A DepositsPlus value date as a calendar date. */
    public static LocalDate toLocalDate(Date legacy) {
        return legacy.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /** A calendar date as a DepositsPlus value date. */
    public static Date toLegacyDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /** "nM" when the maturity is the start plus n whole months, otherwise the length in days, "nD". */
    public static String tenor(LocalDate start, LocalDate maturity) {
        long months = ChronoUnit.MONTHS.between(start, maturity);
        if (months > 0 && start.plusMonths(months).equals(maturity)) {
            return months + "M";
        }
        return ChronoUnit.DAYS.between(start, maturity) + "D";
    }
}
