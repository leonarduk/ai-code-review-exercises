package com.example.mandates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class MandateRegistryTest {

    private static final String CREDITOR = "DE98ZZZ09999999999";
    private static final String REF = "MNDT-2026-0042";

    private final MandateRegistry registry = new MandateRegistry();
    private final Mandate mandate = new Mandate(CREDITOR, REF, "DE89370400440532013000", LocalDate.parse("2026-09-01"));

    @Test
    void aRegisteredMandateIsFound() {
        registry.register(mandate);
        assertEquals(mandate, registry.find(CREDITOR, REF).orElseThrow());
        assertTrue(registry.find("NL91ZZZ123456780000", REF).isEmpty());
    }

    @Test
    void aDuplicateIsRejected() {
        registry.register(mandate);
        assertThrows(IllegalStateException.class, () -> registry.register(mandate));
        assertEquals(1, registry.size());
    }

    @Test
    void theFirstCollectionIsFrstAndLaterOnesRcur() {
        registry.register(mandate);
        assertEquals(DirectDebit.SequenceType.FRST, registry.collect(CREDITOR, REF, new BigDecimal("49.99")).sequenceType());
        assertEquals(DirectDebit.SequenceType.RCUR, registry.collect(CREDITOR, REF, new BigDecimal("49.99")).sequenceType());
    }

    @Test
    void aCancelledMandateIsRefused() {
        registry.register(mandate);
        registry.cancel(CREDITOR, REF);
        assertThrows(NoMandateException.class, () -> registry.collect(CREDITOR, REF, new BigDecimal("49.99")));
    }

    @Test
    void anUnknownMandateIsRefused() {
        assertThrows(NoMandateException.class, () -> registry.collect(CREDITOR, REF, new BigDecimal("49.99")));
    }
}
