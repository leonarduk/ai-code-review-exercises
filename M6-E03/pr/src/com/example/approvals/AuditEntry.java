package com.example.approvals;

import java.time.Instant;
import java.util.List;

/** One immutable line of the approval audit trail: the approvers are those the payment had at that moment. */
public record AuditEntry(long sequence, String paymentId, Action action, Instant at, List<String> approvers) {
}
