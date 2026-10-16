package com.example.payments;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Flags instructions that repeat an earlier one received through any channel (OPS-2741). */
public final class DuplicateCheck {

    private DuplicateCheck() {
    }

    /** The details that make two instructions the same payment. */
    record PaymentKey(String debtorIban, String creditorIban, BigDecimal amount, String currency,
            LocalDate executionDate) {

        static PaymentKey of(PaymentInstruction p) {
            return new PaymentKey(normalise(p.debtorIban()), normalise(p.creditorIban()), p.amount(),
                    p.currency(), p.executionDate());
        }

        private static String normalise(String iban) {
            return iban.replace(" ", "").toUpperCase(Locale.ROOT);
        }
    }

    /** Every instruction that repeats an earlier one, in input order, with the id of the first. */
    public static List<SuspectedDuplicate> check(List<PaymentInstruction> instructions) {
        Map<PaymentKey, String> firstSeen = new HashMap<>();
        List<SuspectedDuplicate> suspected = new ArrayList<>();
        for (PaymentInstruction p : instructions) {
            String first = firstSeen.putIfAbsent(PaymentKey.of(p), p.instructionId());
            if (first != null) {
                suspected.add(new SuspectedDuplicate(p.instructionId(), p.channel(), first));
            }
        }
        return List.copyOf(suspected);
    }

    /** The queue's pending instructions with every suspected duplicate removed. */
    public static List<PaymentInstruction> releasable(IntakeQueue queue) {
        Set<PaymentKey> seen = new HashSet<>();
        return queue.pending().stream().filter(p -> seen.add(PaymentKey.of(p))).toList();
    }
}
