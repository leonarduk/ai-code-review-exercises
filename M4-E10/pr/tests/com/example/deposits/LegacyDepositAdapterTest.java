package com.example.deposits;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.TimeZone;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LegacyDepositAdapterTest {

    private TimeZone original;

    @BeforeEach
    void runInUtcLikeTheBuildAgents() {
        original = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @AfterEach
    void restoreDefaultZone() {
        TimeZone.setDefault(original);
    }

    private static LegacyDepositRecord record(String start, String maturity) {
        LegacyDepositRecord r = new LegacyDepositRecord();
        r.setDealRef("MM-4410");
        r.setCurrencyCode("EUR");
        r.setPrincipal(new BigDecimal("25000000.00"));
        r.setStartDate(Date.from(Instant.parse(start + "T00:00:00Z")));
        r.setMaturityDate(Date.from(Instant.parse(maturity + "T00:00:00Z")));
        r.setBookedTimestamp(Date.from(Instant.parse("2026-01-13T14:05:00Z")));
        return r;
    }

    @Test
    void mapsALegacyRecord() {
        Deposit d = LegacyDepositAdapter.fromLegacy(record("2026-01-15", "2026-04-15"));
        assertEquals("MM-4410", d.dealRef());
        assertEquals(LocalDate.of(2026, 1, 15), d.startDate());
        assertEquals(LocalDate.of(2026, 4, 15), d.maturityDate());
        assertEquals("3M", d.tenor());
        assertEquals(Instant.parse("2026-01-13T14:05:00Z"), d.bookedAt());
    }

    @Test
    void valueDatesSurviveARoundTrip() {
        LocalDate date = LocalDate.of(2026, 6, 30);
        assertEquals(date, LegacyDepositAdapter.toLocalDate(LegacyDepositAdapter.toLegacyDate(date)));
        LegacyDepositRecord r = record("2026-01-15", "2026-07-15");
        LegacyDepositRecord back = LegacyDepositAdapter.toLegacy(LegacyDepositAdapter.fromLegacy(r));
        assertEquals(r.getStartDate(), back.getStartDate());
        assertEquals(r.getMaturityDate(), back.getMaturityDate());
    }

    @Test
    void wholeMonthTenors() {
        assertEquals("1M", LegacyDepositAdapter.tenor(LocalDate.of(2026, 3, 10), LocalDate.of(2026, 4, 10)));
        assertEquals("6M", LegacyDepositAdapter.tenor(LocalDate.of(2026, 6, 15), LocalDate.of(2026, 12, 15)));
        assertEquals("12M", LegacyDepositAdapter.tenor(LocalDate.of(2026, 2, 2), LocalDate.of(2027, 2, 2)));
    }

    @Test
    void brokenDatesAreLabelledInDays() {
        assertEquals("45D", LegacyDepositAdapter.tenor(LocalDate.of(2026, 1, 15), LocalDate.of(2026, 3, 1)));
        assertEquals("7D", LegacyDepositAdapter.tenor(LocalDate.of(2026, 5, 4), LocalDate.of(2026, 5, 11)));
    }
}
