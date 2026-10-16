package com.example.confirms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConfirmationMatcherTest {

    private static final Trade BOOKED = new Trade("T-1001", LocalDate.of(2026, 4, 14),
            OffsetDateTime.parse("2026-04-14T09:30:00+01:00"), new BigDecimal("250000"), new BigDecimal("101.25"));

    @Test
    void parsesALine() {
        Confirmation c = ConfirmationMatcher.parse("T-1001;14/04/2026;2026-04-14T09:30:00+01:00;250000;101.25");
        assertEquals("T-1001", c.tradeId());
        assertEquals(LocalDate.of(2026, 4, 14), c.tradeDate());
        assertEquals(OffsetDateTime.parse("2026-04-14T09:30:00+01:00"), c.executedAt());
        assertEquals(new BigDecimal("250000"), c.quantity());
    }

    @Test
    void matchingConfirmation() {
        Confirmation c = ConfirmationMatcher.parse("T-1001;14/04/2026;2026-04-14T09:30:00+01:00;250000;101.25");
        assertTrue(ConfirmationMatcher.matches(c, BOOKED));
    }

    @Test
    void quantityAndPriceAreComparedByValue() {
        Confirmation c = ConfirmationMatcher.parse("T-1001;14/04/2026;2026-04-14T09:30:00+01:00;250000.00;101.2500");
        assertTrue(ConfirmationMatcher.matches(c, BOOKED));
    }

    @Test
    void reportsEachFieldThatDiffers() {
        Confirmation c = ConfirmationMatcher.parse("T-1001;15/04/2026;2026-04-14T09:30:00+01:00;250000;101.30");
        assertEquals(List.of("tradeDate", "price"), ConfirmationMatcher.mismatches(c, BOOKED));
    }

    @Test
    void rejectsAnInvalidTradeDate() {
        assertThrows(DateTimeParseException.class,
                () -> ConfirmationMatcher.parse("T-1001;32/04/2026;2026-04-14T09:30:00+01:00;250000;101.25"));
    }

    @Test
    void rejectsALineWithMissingFields() {
        assertThrows(IllegalArgumentException.class,
                () -> ConfirmationMatcher.parse("T-1001;14/04/2026;250000;101.25"));
    }
}
