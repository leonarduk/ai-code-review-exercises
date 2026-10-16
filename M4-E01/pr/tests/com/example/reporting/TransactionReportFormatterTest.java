package com.example.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

class TransactionReportFormatterTest {

    private static final ZoneId LONDON = ZoneId.of("Europe/London");

    private static Execution execution(ZonedDateTime executedAt, String quantity, String price) {
        return new Execution("T-1001", "GB00B03MLX29", new BigDecimal(quantity), new BigDecimal(price), executedAt);
    }

    @Test
    void tradingDateTimeHasMicrosecondPrecision() {
        Execution e = execution(ZonedDateTime.of(2026, 2, 10, 9, 15, 30, 123_456_789, LONDON), "500", "27.50");
        assertEquals("2026-02-10T09:15:30.123456Z", TransactionReportFormatter.tradingDateTime(e));
    }

    @Test
    void tradeDateIsTheCalendarDate() {
        Execution e = execution(ZonedDateTime.of(2026, 6, 18, 14, 0, 0, 0, ZoneId.of("UTC")), "500", "27.50");
        assertEquals("20260618", TransactionReportFormatter.tradeDate(e));
    }

    @Test
    void numbersHaveNoTrailingZerosOrExponent() {
        Execution e = execution(ZonedDateTime.of(2026, 2, 10, 9, 15, 30, 0, LONDON), "1000.00", "100.00");
        assertEquals("1000", TransactionReportFormatter.quantity(e));
        assertEquals("100", TransactionReportFormatter.price(e));
    }

    @Test
    void rowJoinsTheFieldsInOrder() {
        Execution e = execution(ZonedDateTime.of(2026, 1, 20, 16, 29, 59, 500_000_000, LONDON), "250", "3.1400");
        assertEquals("T-1001;GB00B03MLX29;20260120;2026-01-20T16:29:59.500000Z;250;3.14",
                TransactionReportFormatter.row(e));
    }
}
