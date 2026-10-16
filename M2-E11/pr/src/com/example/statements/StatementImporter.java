package com.example.statements;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Imports the bank's statement export for reconciliation. Each line is
 * {@code date;description;amount}, e.g. {@code 2026-03-02;Card payment;-1,250.50}.
 */
public final class StatementImporter {

    private static final int FIELDS_PER_LINE = 3;

    private StatementImporter() {
    }

    public static StatementLine parseLine(String line) {
        String[] fields = line.split(";", -1);
        if (fields.length != FIELDS_PER_LINE) {
            throw new StatementFormatException("expected " + FIELDS_PER_LINE + " fields: " + line);
        }
        LocalDate date;
        try {
            date = LocalDate.parse(fields[0].trim());
        } catch (DateTimeParseException e) {
            throw new StatementFormatException("not a date: '" + fields[0] + "'", e);
        }
        return new StatementLine(date, fields[1].trim(), parseAmount(fields[2]));
    }

    /**
     * Parses a UK-formatted amount from the export, such as {@code 1,250.50}
     * or {@code -42.50}. The export always writes thousands separators.
     */
    public static BigDecimal parseAmount(String text) {
        String trimmed = text.trim();
        NumberFormat format = NumberFormat.getNumberInstance(Locale.UK);
        try {
            Number number = format.parse(trimmed);
            return BigDecimal.valueOf(number.doubleValue());
        } catch (ParseException e) {
            throw new StatementFormatException("not an amount: '" + trimmed + "'", e);
        }
    }
}
