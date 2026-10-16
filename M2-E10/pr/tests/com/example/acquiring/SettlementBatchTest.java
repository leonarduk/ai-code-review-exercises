package com.example.acquiring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class SettlementBatchTest {

    @Test
    void feeOnASale() {
        SettlementBatch batch = new SettlementBatch(List.of(), 150);
        // 100.00 EUR at 1.50%
        assertEquals(150, batch.feeMinor(new Transaction("S-1", 10_000)));
        // 25.50 EUR at 1.50% = 38.25 cents
        assertEquals(38, batch.feeMinor(new Transaction("S-2", 2_550)));
    }

    @Test
    void aRefundReturnsTheFee() {
        SettlementBatch batch = new SettlementBatch(List.of(), 150);
        assertEquals(-150, batch.feeMinor(new Transaction("R-1", -10_000)));
    }

    @Test
    void netIsGrossMinusFees() {
        SettlementBatch batch = new SettlementBatch(List.of(
                new Transaction("S-1", 10_000),
                new Transaction("S-2", 2_550),
                new Transaction("R-1", -10_000)), 150);
        assertEquals(2_550, batch.grossMinor());
        assertEquals(38, batch.feesMinor());
        assertEquals(2_512, batch.netMinor());
    }

    @Test
    void anEmptyBatchSettlesNothing() {
        SettlementBatch batch = new SettlementBatch(List.of(), 150);
        assertEquals(0, batch.feesMinor());
        assertEquals(0, batch.netMinor());
    }

    @Test
    void mdrOutOfRangeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new SettlementBatch(List.of(), -1));
        assertThrows(IllegalArgumentException.class, () -> new SettlementBatch(List.of(), 1_001));
    }
}
