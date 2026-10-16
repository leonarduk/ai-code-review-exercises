package com.example.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class GroupHeaderTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-15T09:30:05Z"), ZoneOffset.UTC);

    private static CreditTransfer eur(String id, String amount) {
        return new CreditTransfer(id, "DE89370400440532013000", new BigDecimal(amount), "EUR");
    }

    @Test
    void headerLineForTwoTransfers() {
        GroupHeader header = GroupHeader.of("EUR", List.of(eur("E2E-1", "100.00"), eur("E2E-2", "25.50")), CLOCK, 3);
        assertEquals("MSG-20261015093005-0003|2026-10-15T09:30:05|2|125.50|EUR", header.line());
    }

    @Test
    void controlSumAndCount() {
        GroupHeader header = GroupHeader.of("EUR",
                List.of(eur("E2E-1", "0.10"), eur("E2E-2", "0.20"), eur("E2E-3", "1000.00")), CLOCK, 1);
        assertEquals(3, header.numberOfTransactions());
        assertEquals(new BigDecimal("1000.30"), header.controlSum());
    }

    @Test
    void aTransferInAnotherCurrencyIsRejected() {
        CreditTransfer usd = new CreditTransfer("E2E-2", "US64SVBKUS6S3300958879", new BigDecimal("10.00"), "USD");
        assertThrows(IllegalArgumentException.class,
                () -> GroupHeader.of("EUR", List.of(eur("E2E-1", "10.00"), usd), CLOCK, 1));
    }

    @Test
    void anEmptyBatchIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> GroupHeader.of("EUR", List.of(), CLOCK, 1));
    }

    @Test
    void sequenceMustFitFourDigits() {
        assertThrows(IllegalArgumentException.class,
                () -> GroupHeader.of("EUR", List.of(eur("E2E-1", "10.00")), CLOCK, 10000));
    }
}
