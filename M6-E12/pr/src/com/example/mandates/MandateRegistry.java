package com.example.mandates;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** The mandates our debtors have signed, by creditor id and mandate reference (DD-214). */
public final class MandateRegistry {

    private final Map<MandateKey, Mandate> mandates = new HashMap<>();
    private final Set<MandateKey> collected = new HashSet<>();

    public void register(Mandate mandate) {
        if (mandates.putIfAbsent(MandateKey.of(mandate), mandate) != null) {
            throw new IllegalStateException("mandate already registered: " + mandate.reference());
        }
    }

    public Optional<Mandate> find(String creditorId, String reference) {
        return Optional.ofNullable(mandates.get(new MandateKey(creditorId, reference)));
    }

    /** Cancels a mandate at the debtor's request; later collections under it are refused. */
    public void cancel(String creditorId, String reference) {
        MandateKey key = new MandateKey(creditorId, reference);
        if (mandates.remove(key) == null) {
            throw new NoMandateException(creditorId, reference);
        }
        collected.remove(key);
    }

    public DirectDebit collect(String creditorId, String reference, BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("a collection must be for a positive amount");
        }
        MandateKey key = new MandateKey(creditorId, reference);
        Mandate mandate = find(creditorId, reference).orElseThrow(() -> new NoMandateException(creditorId, reference));
        DirectDebit.SequenceType type = collected.add(key) ? DirectDebit.SequenceType.FRST : DirectDebit.SequenceType.RCUR;
        return new DirectDebit(mandate, amount, type);
    }

    public int size() {
        return mandates.size();
    }
}
