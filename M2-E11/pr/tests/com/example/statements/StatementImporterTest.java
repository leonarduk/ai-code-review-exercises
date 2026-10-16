package com.example.statements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class StatementImporterTest {

    private static void assertAmount(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual), () -> expected + " but was " + actual);
    }

    @Test
    void parsesThousandsSeparators() {
        assertAmount("1250.50", StatementImporter.parseAmount("1,250.50"));
        assertAmount("1234567.89", StatementImporter.parseAmount("1,234,567.89"));
    }

    @Test
    void parsesDebitsAndSmallAmounts() {
        assertAmount("-42.50", StatementImporter.parseAmount("-42.50"));
        assertAmount("0.99", StatementImporter.parseAmount(" 0.99 "));
    }

    @Test
    void parsesAWholeLine() {
        StatementLine line = StatementImporter.parseLine("2026-03-02;Card payment;-1,250.50");
        assertEquals(LocalDate.of(2026, 3, 2), line.date());
        assertEquals("Card payment", line.description());
        assertAmount("-1250.50", line.amount());
    }

    @Test
    void rejectsAnAmountThatIsNotANumber() {
        assertThrows(StatementFormatException.class, () -> StatementImporter.parseAmount("n/a"));
        assertThrows(StatementFormatException.class, () -> StatementImporter.parseAmount(""));
    }

    @Test
    void rejectsALineWithMissingFields() {
        assertThrows(StatementFormatException.class,
                () -> StatementImporter.parseLine("2026-03-02;Card payment"));
    }
}
