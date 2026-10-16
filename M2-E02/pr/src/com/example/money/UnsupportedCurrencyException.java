package com.example.money;

/** A currency code that is not one of our settlement currencies. */
public class UnsupportedCurrencyException extends IllegalArgumentException {

    public UnsupportedCurrencyException(String currencyCode, Throwable cause) {
        super("unsupported settlement currency: " + currencyCode, cause);
    }
}
