package com.example.fees;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MonthlyFeeCalculatorTest {

    private final MonthlyFeeCalculator calculator =
            new MonthlyFeeCalculator(new FeeSchedule(0.006));

    @ParameterizedTest
    @CsvSource({
        "1000000.00, 500.00",
        "250000.00, 125.00",
        "1234567.89, 617.28",
    })
    void monthlyFeeIsOneTwelfthOfTheAnnualRate(String aum, String expected) {
        assertEquals(new BigDecimal(expected), calculator.monthlyFee(new BigDecimal(aum)));
    }

    @Test
    void invoiceLineCarriesTheFee() {
        InvoiceLine line = calculator.invoiceLine("ACC-1", new BigDecimal("1000000.00")).orElseThrow();
        assertEquals(new BigDecimal("500.00"), line.amount());
        assertEquals("ACC-1", line.accountId());
    }

    @Test
    void negativeAssetsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.monthlyFee(new BigDecimal("-1")));
    }
}
