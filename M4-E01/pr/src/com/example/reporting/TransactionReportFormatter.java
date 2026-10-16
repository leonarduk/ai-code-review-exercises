package com.example.reporting;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/** Renders executions as rows of the daily transaction report (REG-2207). */
public final class TransactionReportFormatter {

    private static final DateTimeFormatter TRADING_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
    private static final DateTimeFormatter TRADE_DATE = DateTimeFormatter.ofPattern("YYYYMMdd");
    private static final String SEPARATOR = ";";

    private TransactionReportFormatter() {
    }

    /** The execution instant in UTC, to the microsecond. */
    public static String tradingDateTime(Execution execution) {
        return TRADING_DATE_TIME.format(inUtc(execution));
    }

    /** The UTC calendar date of the execution. */
    public static String tradeDate(Execution execution) {
        return TRADE_DATE.format(inUtc(execution));
    }

    /** Decimal price without trailing zeros or exponent notation. */
    public static String price(Execution execution) {
        return execution.price().stripTrailingZeros().toPlainString();
    }

    public static String quantity(Execution execution) {
        return execution.quantity().stripTrailingZeros().toPlainString();
    }

    /** One report row: id; ISIN; trade date; trading date time; quantity; price. */
    public static String row(Execution execution) {
        return String.join(SEPARATOR,
                execution.transactionId(),
                execution.isin(),
                tradeDate(execution),
                tradingDateTime(execution),
                quantity(execution),
                price(execution));
    }

    private static ZonedDateTime inUtc(Execution execution) {
        return execution.executedAt().withZoneSameLocal(ZoneOffset.UTC);
    }
}
