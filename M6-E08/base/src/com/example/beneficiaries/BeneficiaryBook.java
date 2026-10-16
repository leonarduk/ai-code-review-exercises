package com.example.beneficiaries;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** One customer's beneficiaries, by IBAN. */
public final class BeneficiaryBook {

    private final Map<String, Beneficiary> byIban = new HashMap<>();

    public Optional<Beneficiary> find(String iban) {
        return Optional.ofNullable(byIban.get(iban));
    }

    /** Adds a beneficiary (from online banking or the branch), replacing any entry for the IBAN. */
    public Beneficiary add(String iban, String name, Instant addedAt) {
        Beneficiary beneficiary = new Beneficiary(iban, name, addedAt);
        byIban.put(iban, beneficiary);
        return beneficiary;
    }

    public int size() {
        return byIban.size();
    }
}
