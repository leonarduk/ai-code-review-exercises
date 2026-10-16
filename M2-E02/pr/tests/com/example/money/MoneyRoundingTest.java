package com.example.money;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MoneyRoundingTest {

    @ParameterizedTest
    @CsvSource({
        "2.345, USD, 2.34",
        "2.355, EUR, 2.36",
        "1234.5, JPY, 1234",
        "1.23456, KWD, 1.235",
        "10, chf, 10.00",
    })
    void roundsToTheCurrencysMinorUnits(String amount, String currency, String expected) {
        assertEquals(new BigDecimal(expected), MoneyRounding.round(new BigDecimal(amount), currency));
    }

    @ParameterizedTest
    @CsvSource({
        "1234.56, USD, 1234.56 USD",
        "1.2345, KWD, 1.234 KWD",
        "1234.5, JPY, 1234 JPY",
    })
    void formatsForStatements(String amount, String currency, String expected) {
        assertEquals(expected, MoneyRounding.format(new BigDecimal(amount), currency));
    }

    @Test
    void unknownCurrencyCodeIsRejected() {
        assertThrows(UnsupportedCurrencyException.class,
                () -> MoneyRounding.round(BigDecimal.ONE, "EUD"));
    }
}
