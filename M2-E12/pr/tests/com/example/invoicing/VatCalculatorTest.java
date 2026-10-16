package com.example.invoicing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class VatCalculatorTest {

    private static final BigDecimal STANDARD = new BigDecimal("0.20");
    private static final BigDecimal REDUCED = new BigDecimal("0.05");

    @Test
    void vatOnANetAmount() {
        assertEquals(new BigDecimal("25.00"), VatCalculator.vatOnNet(new BigDecimal("125.00"), STANDARD));
        // 19.99 x 5% = 0.9995
        assertEquals(new BigDecimal("1.00"), VatCalculator.vatOnNet(new BigDecimal("19.99"), REDUCED));
    }

    @Test
    void zeroRatedLinesCarryNoVat() {
        assertEquals(new BigDecimal("0.00"), VatCalculator.vatOnNet(new BigDecimal("80.00"), BigDecimal.ZERO));
    }

    @Test
    void vatInAVatInclusiveAmount() {
        assertEquals(new BigDecimal("20.00"), VatCalculator.vatInGross(new BigDecimal("120.00"), STANDARD));
        assertEquals(new BigDecimal("5.00"), VatCalculator.vatInGross(new BigDecimal("105.00"), REDUCED));
    }

    @Test
    void invoiceVatSumsTheLines() {
        List<InvoiceLine> lines = List.of(
                new InvoiceLine("Consulting", new BigDecimal("1000.00"), STANDARD),
                new InvoiceLine("Child car seat", new BigDecimal("80.00"), REDUCED),
                new InvoiceLine("Books", new BigDecimal("45.00"), BigDecimal.ZERO));
        assertEquals(new BigDecimal("204.00"), VatCalculator.invoiceVat(lines));
    }

    @Test
    void negativeRatesAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> VatCalculator.vatOnNet(new BigDecimal("10.00"), new BigDecimal("-0.20")));
    }
}
