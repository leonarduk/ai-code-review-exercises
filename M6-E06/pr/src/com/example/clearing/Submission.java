package com.example.clearing;

import java.util.List;
import java.util.Locale;

/** A clearing submission: the payment records, one fixed-width line each. */
public final class Submission {

    private Submission() {
    }

    public static List<String> records(List<Payment> payments) {
        return payments.stream()
                .map(p -> "PAY" + p.sortCode() + p.accountNumber()
                        + String.format(Locale.ROOT, "%011d", p.amountPence()) + p.reference())
                .toList();
    }
}
