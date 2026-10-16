package com.example.fx;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts between currencies by crossing through USD.
 *
 * <p>Quotes use market convention {@code BASE/QUOTE}: the price of one unit
 * of BASE in QUOTE, e.g. {@code EUR/USD 1.0850} or {@code USD/JPY 151.20}.
 */
public final class CrossRateConverter {

    private static final String USD = "USD";
    private static final int MONEY_SCALE = 2;
    private static final MathContext INTERMEDIATE = MathContext.DECIMAL32;

    private final Map<String, BigDecimal> usdPerUnit = new HashMap<>();

    public CrossRateConverter(Map<String, BigDecimal> quotes) {
        usdPerUnit.put(USD, BigDecimal.ONE);
        quotes.forEach((pair, rate) -> {
            String[] currencies = pair.split("/");
            if (currencies.length != 2) {
                throw new IllegalArgumentException("expected a BASE/QUOTE pair, got " + pair);
            }
            if (currencies[1].equals(USD)) {
                usdPerUnit.put(currencies[0], rate);
            } else if (currencies[0].equals(USD)) {
                usdPerUnit.put(currencies[1], BigDecimal.ONE.divide(rate, MONEY_SCALE, RoundingMode.HALF_EVEN));
            } else {
                throw new IllegalArgumentException("only USD pairs are supported, got " + pair);
            }
        });
    }

    /** Converts {@code amount}, rounding only the result to the target's minor units. */
    public BigDecimal convert(BigDecimal amount, String source, String target) {
        BigDecimal usd = amount.multiply(usdPerUnit(source), INTERMEDIATE);
        BigDecimal converted = usd.divide(usdPerUnit(target), INTERMEDIATE);
        int minorUnits = Currency.getInstance(target).getDefaultFractionDigits();
        return converted.setScale(minorUnits, RoundingMode.HALF_EVEN);
    }

    private BigDecimal usdPerUnit(String currency) {
        BigDecimal rate = usdPerUnit.get(currency);
        if (rate == null) {
            throw new IllegalArgumentException("no USD quote for " + currency);
        }
        return rate;
    }
}
