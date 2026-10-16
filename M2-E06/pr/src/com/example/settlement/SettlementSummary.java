package com.example.settlement;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Summarises a settlement file as the net amount booked at each tax rate.
 *
 * <p>Each line is {@code tradeId,netAmount,taxRate}, e.g.
 * {@code T-1001,1250.00,0.20}.
 */
public final class SettlementSummary {

    private static final int FIELDS_PER_LINE = 3;

    private SettlementSummary() {
    }

    public static Map<BigDecimal, BigDecimal> netByTaxRate(List<String> lines) {
        Map<BigDecimal, BigDecimal> totals = new HashMap<>();
        for (String line : lines) {
            String[] fields = line.split(",");
            if (fields.length != FIELDS_PER_LINE) {
                throw new SettlementFileException(
                        "expected " + FIELDS_PER_LINE + " fields but found " + fields.length + ": " + line);
            }
            BigDecimal netAmount = parseDecimal(fields[1]);
            BigDecimal taxRate = parseDecimal(fields[2]);
            totals.merge(taxRate, netAmount, BigDecimal::add);
        }
        return totals;
    }

    static BigDecimal parseDecimal(String text) {
        try {
            return new BigDecimal(text.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
