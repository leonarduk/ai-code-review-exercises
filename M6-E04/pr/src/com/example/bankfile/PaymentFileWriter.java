package com.example.bankfile;

import java.util.List;
import java.util.Set;

/** Writes the daily payment file for the bank's host-to-host channel. */
public final class PaymentFileWriter {

    public static final String HEADER = "reference,iban,amount,currency";
    private static final Set<String> CURRENCIES = Set.of("EUR", "GBP", "USD");

    private PaymentFileWriter() {
    }

    public static String line(OutgoingPayment p) {
        if (!CURRENCIES.contains(p.currency())) {
            throw new IllegalArgumentException(p.reference() + ": the bank file takes EUR, GBP and USD only");
        }
        if (p.amount().signum() <= 0 || p.amount().scale() > 2) {
            throw new IllegalArgumentException(p.reference() + ": amount must be positive with at most two decimals");
        }
        if (p.reference().contains(",")) {
            throw new IllegalArgumentException(p.reference() + ": a reference cannot contain a comma");
        }
        return String.join(",", p.reference(), p.iban(), String.format("%.2f", p.amount()), p.currency());
    }

    public static List<String> file(List<OutgoingPayment> payments) {
        return java.util.stream.Stream.concat(java.util.stream.Stream.of(HEADER),
                payments.stream().map(PaymentFileWriter::line)).toList();
    }
}
