package com.example.options;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class OptionExpiryTest {

    private static Clock at(String instant) {
        return Clock.fixed(Instant.parse(instant), ZoneOffset.UTC);
    }

    @Test
    void newYorkCutInWinter() {
        OptionExpiry expiry = new OptionExpiry(LocalDate.of(2026, 1, 16), Cut.NEW_YORK);
        assertEquals(Instant.parse("2026-01-16T15:00:00Z"), expiry.instant());
    }

    @Test
    void tokyoCut() {
        OptionExpiry expiry = new OptionExpiry(LocalDate.of(2026, 6, 19), Cut.TOKYO);
        assertEquals(Instant.parse("2026-06-19T06:00:00Z"), expiry.instant());
    }

    @Test
    void expiredFromTheCutOnwards() {
        OptionExpiry expiry = new OptionExpiry(LocalDate.of(2026, 1, 16), Cut.NEW_YORK);
        assertFalse(expiry.isExpired(at("2026-01-16T14:59:59Z")));
        assertTrue(expiry.isExpired(at("2026-01-16T15:00:00Z")));
    }

    @Test
    void yearsToExpiry() {
        OptionExpiry expiry = new OptionExpiry(LocalDate.of(2026, 1, 16), Cut.NEW_YORK);
        assertEquals(10.0 / 365, expiry.yearsToExpiry(at("2026-01-06T15:00:00Z")), 1e-12);
        assertEquals(0.0, expiry.yearsToExpiry(at("2026-01-17T00:00:00Z")));
    }
}
