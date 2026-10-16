package com.example.beneficiaries;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** One customer's beneficiaries, by IBAN. */
public final class BeneficiaryBook {

    private final Map<String, Beneficiary> byIban = new HashMap<>();

    /** A book from the nightly export, oldest rows first; for an IBAN listed more than once, the last row wins. */
    public static BeneficiaryBook load(List<Beneficiary> exportRows) {
        BeneficiaryBook book = new BeneficiaryBook();
        book.byIban.putAll(exportRows.stream().collect(Collectors.toMap(Beneficiary::iban, b -> b)));
        return book;
    }

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
