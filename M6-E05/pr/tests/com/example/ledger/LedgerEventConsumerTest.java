package com.example.ledger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LedgerEventConsumerTest {

    private final Ledger ledger = new Ledger();
    private final LedgerEventConsumer consumer = new LedgerEventConsumer(ledger);
    private final AtomicInteger acks = new AtomicInteger();

    @BeforeEach
    void openAccounts() {
        ledger.open("ACC-1");
        ledger.open("ACC-2");
    }

    private void deliver(String account, long sequence, String amount) {
        consumer.onEvent(new LedgerEvent("E-" + account + "-" + sequence, account, sequence, new BigDecimal(amount)),
                acks::incrementAndGet);
    }

    @Test
    void eventsInSequenceAreApplied() {
        deliver("ACC-1", 1, "100.00");
        deliver("ACC-1", 2, "-30.00");
        deliver("ACC-1", 3, "5.50");
        assertEquals(new BigDecimal("75.50"), ledger.balance("ACC-1"));
        assertEquals(3, acks.get());
        assertEquals(4, consumer.nextExpected("ACC-1"));
    }

    @Test
    void aRedeliveryIsIgnoredAndAcknowledged() {
        deliver("ACC-1", 1, "100.00");
        deliver("ACC-1", 2, "50.00");
        deliver("ACC-1", 2, "50.00");
        assertEquals(new BigDecimal("150.00"), ledger.balance("ACC-1"));
        assertEquals(3, acks.get());
    }

    @Test
    void anEventAheadOfItsTurnIsRejected() {
        deliver("ACC-1", 1, "100.00");
        assertThrows(OutOfSequenceException.class, () -> deliver("ACC-1", 3, "10.00"));
        assertEquals(new BigDecimal("100.00"), ledger.balance("ACC-1"));
    }

    @Test
    void accountsHaveTheirOwnSequences() {
        deliver("ACC-1", 1, "10.00");
        deliver("ACC-2", 1, "20.00");
        deliver("ACC-1", 2, "10.00");
        assertEquals(new BigDecimal("20.00"), ledger.balance("ACC-1"));
        assertEquals(new BigDecimal("20.00"), ledger.balance("ACC-2"));
    }

    @Test
    void aBusyAccountKeepsApplyingEvents() {
        for (long s = 1; s <= 100; s++) {
            deliver("ACC-1", s, "1.00");
        }
        assertEquals(new BigDecimal("100.00"), ledger.balance("ACC-1"));
        assertEquals(101, consumer.nextExpected("ACC-1"));
    }
}
