package com.example.approvals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class PaymentApprovalsTest {

    private static final Instant NOON = Instant.parse("2026-10-15T12:00:00Z");

    private final AuditTrail trail = new AuditTrail(Clock.fixed(NOON, ZoneOffset.UTC));
    private final PaymentApprovals approvals = new PaymentApprovals(trail);

    @Test
    void theFirstApprovalIsRecorded() {
        assertFalse(approvals.approve("PAY-1", "ana"));
        assertEquals(List.of(new AuditEntry(1, "PAY-1", Action.APPROVED, NOON, List.of("ana"))),
                trail.entries("PAY-1"));
    }

    @Test
    void theSecondApproverReleasesThePayment() {
        approvals.approve("PAY-1", "ana");
        assertTrue(approvals.approve("PAY-1", "ben"));
        List<AuditEntry> entries = trail.entries("PAY-1");
        assertEquals(3, entries.size());
        assertEquals(Action.RELEASED, entries.get(2).action());
        assertEquals(List.of("ana", "ben"), entries.get(2).approvers());
    }

    @Test
    void theSameApproverCannotApproveTwice() {
        approvals.approve("PAY-1", "ana");
        assertThrows(IllegalStateException.class, () -> approvals.approve("PAY-1", "ana"));
    }

    @Test
    void aPaymentWithNoApprovalsHasAnEmptyTrail() {
        assertEquals(List.of(), trail.entries("PAY-9"));
    }

    @Test
    void sequenceNumbersRunAcrossPayments() {
        approvals.approve("PAY-1", "ana");
        approvals.approve("PAY-2", "ana");
        assertEquals(2, trail.entries("PAY-2").get(0).sequence());
        assertEquals(2, trail.size());
    }
}
