package com.example.fx;

import java.util.Objects;

/** A currency pair such as EUR/USD: base currency first, quote currency second. */
public record CurrencyPair(String base, String quote) {

    public CurrencyPair {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(quote, "quote");
        if (base.equals(quote)) {
            throw new IllegalArgumentException("base and quote must differ: " + base);
        }
    }

    /** Parses "EURUSD" or "EUR/USD". */
    public static CurrencyPair parse(String text) {
        String code = text.replace("/", "").trim();
        if (code.length() != 6) {
            throw new IllegalArgumentException("not a currency pair: " + text);
        }
        return new CurrencyPair(code.substring(0, 3), code.substring(3));
    }
}
