package com.example.mandates;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** The mandates our debtors have signed, by creditor id and mandate reference. */
public final class MandateRegistry {

    private final Map<String, Mandate> mandates = new HashMap<>();

    public void register(Mandate mandate) {
        String key = mandate.creditorId() + "/" + mandate.reference();
        if (mandates.putIfAbsent(key, mandate) != null) {
            throw new IllegalStateException("mandate already registered: " + key);
        }
    }

    public Optional<Mandate> find(String creditorId, String reference) {
        return Optional.ofNullable(mandates.get(creditorId + "/" + reference));
    }

    public int size() {
        return mandates.size();
    }
}
