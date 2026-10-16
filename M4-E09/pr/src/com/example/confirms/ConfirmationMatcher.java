package com.example.confirms;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Parses counterparty confirmation lines and matches them against booked trades (OPS-2741). */
public final class ConfirmationMatcher {

    private static final DateTimeFormatter TRADE_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int FIELDS = 5;

    private ConfirmationMatcher() {
    }

    /** trade id; trade date (day/month/year); execution time (ISO 8601 with offset); quantity; price */
    public static Confirmation parse(String line) {
        String[] fields = line.split(";", -1);
        if (fields.length != FIELDS) {
            throw new IllegalArgumentException("expected " + FIELDS + " fields, got " + fields.length + ": " + line);
        }
        return new Confirmation(
                fields[0].strip(),
                LocalDate.parse(fields[1].strip(), TRADE_DATE),
                OffsetDateTime.parse(fields[2].strip()),
                new BigDecimal(fields[3].strip()),
                new BigDecimal(fields[4].strip()));
    }

    /** The fields on which the confirmation and the trade disagree, in line order; empty when they match. */
    public static List<String> mismatches(Confirmation confirmation, Trade trade) {
        List<String> fields = new ArrayList<>();
        if (!confirmation.tradeId().equals(trade.tradeId())) {
            fields.add("tradeId");
        }
        if (!confirmation.tradeDate().equals(trade.tradeDate())) {
            fields.add("tradeDate");
        }
        if (!confirmation.executedAt().equals(trade.executedAt())) {
            fields.add("executedAt");
        }
        if (confirmation.quantity().compareTo(trade.quantity()) != 0) {
            fields.add("quantity");
        }
        if (confirmation.price().compareTo(trade.price()) != 0) {
            fields.add("price");
        }
        return List.copyOf(fields);
    }

    public static boolean matches(Confirmation confirmation, Trade trade) {
        return mismatches(confirmation, trade).isEmpty();
    }
}
