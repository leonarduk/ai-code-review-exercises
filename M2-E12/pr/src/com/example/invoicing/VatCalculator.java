package com.example.invoicing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/** UK VAT on invoice lines and on VAT-inclusive receipts. */
public final class VatCalculator {

    private static final int PENCE = 2;

    private VatCalculator() {
    }

    /** The VAT on a net amount, rounded half-up to the penny. */
    public static BigDecimal vatOnNet(BigDecimal net, BigDecimal rate) {
        Objects.requireNonNull(net, "net");
        checkRate(rate);
        return net.multiply(rate).setScale(PENCE, RoundingMode.HALF_UP);
    }

    /**
     * The VAT contained in a VAT-inclusive amount, gross x rate / (1 + rate),
     * rounded half-up to the penny. At the standard rate this is 1/6 of the
     * gross.
     */
    public static BigDecimal vatInGross(BigDecimal gross, BigDecimal rate) {
        Objects.requireNonNull(gross, "gross");
        checkRate(rate);
        return gross.multiply(rate).divide(BigDecimal.ONE.add(rate), PENCE, RoundingMode.HALF_UP);
    }

    /** The VAT on an invoice: each line's VAT, rounded, then summed. */
    public static BigDecimal invoiceVat(List<InvoiceLine> lines) {
        Objects.requireNonNull(lines, "lines");
        return lines.stream()
                .map(line -> vatOnNet(line.net(), line.vatRate()))
                .reduce(BigDecimal.ZERO.setScale(PENCE), BigDecimal::add);
    }

    private static void checkRate(BigDecimal rate) {
        Objects.requireNonNull(rate, "rate");
        if (rate.signum() < 0 || rate.compareTo(BigDecimal.ONE) == 1) {
            throw new IllegalArgumentException("VAT rate must be a fraction from 0 to 1: " + rate);
        }
    }
}
