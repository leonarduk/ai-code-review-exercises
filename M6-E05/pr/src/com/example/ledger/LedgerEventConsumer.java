package com.example.ledger;

import java.util.HashMap;
import java.util.Map;

/** Applies posting events to the ledger in per-account sequence order (LED-877). */
public final class LedgerEventConsumer {

    private final Ledger ledger;
    private final Map<String, Long> nextExpected = new HashMap<>();

    public LedgerEventConsumer(Ledger ledger) {
        this.ledger = ledger;
    }

    public void onEvent(LedgerEvent event, Acknowledgement ack) {
        try {
            process(event);
        } finally {
            ack.acknowledge();
        }
    }

    private void process(LedgerEvent event) {
        Long expected = nextExpected.getOrDefault(event.accountId(), 1L);
        if (event.sequence() < expected) {
            return;
        }
        if (event.sequence() != expected) {
            throw new OutOfSequenceException(event.accountId(), expected, event.sequence());
        }
        ledger.post(event.accountId(), event.amount());
        nextExpected.put(event.accountId(), expected + 1);
    }

    /** The next sequence number this consumer will apply for the account. */
    public long nextExpected(String accountId) {
        return nextExpected.getOrDefault(accountId, 1L);
    }
}
