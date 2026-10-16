package com.example.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

/** Rounding and display of amounts in each currency's ISO 4217 minor units. */
public final class MoneyRounding {

    private MoneyRounding() {
    }

    /** Rounds {@code amount} to the minor units of {@code currencyCode}, half-even. */
    public static BigDecimal round(BigDecimal amount, String currencyCode) {
        Objects.requireNonNull(amount, "amount");
        Currency currency = settlementCurrency(currencyCode);
        return amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_EVEN);
    }

    /** Formats {@code amount} for client statements, e.g. {@code "1250.50 USD"}. */
    public static String format(BigDecimal amount, String currencyCode) {
        BigDecimal rounded = round(amount, currencyCode);
        return rounded.stripTrailingZeros().toString() + " " + currencyCode.toUpperCase(Locale.ROOT);
    }

    private static Currency settlementCurrency(String currencyCode) {
        Objects.requireNonNull(currencyCode, "currencyCode");
        try {
            return Currency.getInstance(currencyCode.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new UnsupportedCurrencyException(currencyCode, e);
        }
    }
}
