package com.example.mandates;

/** Identifies a mandate: the creditor id exactly, the mandate reference in any letter case. */
record MandateKey(String creditorId, String reference) {

    static MandateKey of(Mandate mandate) {
        return new MandateKey(mandate.creditorId(), mandate.reference());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MandateKey k
                && creditorId.equals(k.creditorId)
                && reference.equalsIgnoreCase(k.reference);
    }
}
