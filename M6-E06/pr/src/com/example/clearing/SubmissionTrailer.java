package com.example.clearing;

import java.util.List;
import java.util.Locale;

/** The trailer record of a clearing submission: payment count and total in pence (CLR-219). */
public record SubmissionTrailer(int count, long totalPence) {

    static final int MAX_COUNT = 9_999_999;
    static final long MAX_TOTAL_PENCE = 99_999_999_999_999L;

    public static SubmissionTrailer of(List<Payment> payments) {
        if (payments.isEmpty()) {
            throw new IllegalArgumentException("a submission needs at least one payment");
        }
        if (payments.size() > MAX_COUNT) {
            throw new IllegalArgumentException("too many payments for one submission: " + payments.size());
        }
        var total = 0;
        for (Payment p : payments) {
            if (p.amountPence() <= 0) {
                throw new IllegalArgumentException(p.reference() + ": amount must be positive");
            }
            total += p.amountPence();
        }
        if (total > MAX_TOTAL_PENCE) {
            throw new IllegalArgumentException("submission total does not fit the trailer: " + total);
        }
        return new SubmissionTrailer(payments.size(), total);
    }

    /** TRL, the count as 7 digits and the total in pence as 14 digits. */
    public String line() {
        return "TRL" + String.format(Locale.ROOT, "%07d%014d", count, totalPence);
    }

    /** The submission's records followed by its trailer. */
    public static List<String> file(List<Payment> payments) {
        SubmissionTrailer trailer = of(payments);
        return java.util.stream.Stream.concat(Submission.records(payments).stream(),
                java.util.stream.Stream.of(trailer.line())).toList();
    }
}
