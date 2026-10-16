package com.example.bankfile;

import java.util.List;

/** Writes the daily payment file for the bank's host-to-host channel. */
public final class PaymentFileWriter {

    public static final String HEADER = "reference,iban,amount,currency";

    private PaymentFileWriter() {
    }

    public static String line(OutgoingPayment p) {
        return String.join(",", p.reference(), p.iban(), p.amount().toString(), p.currency());
    }

    public static List<String> file(List<OutgoingPayment> payments) {
        return java.util.stream.Stream.concat(java.util.stream.Stream.of(HEADER),
                payments.stream().map(PaymentFileWriter::line)).toList();
    }
}
