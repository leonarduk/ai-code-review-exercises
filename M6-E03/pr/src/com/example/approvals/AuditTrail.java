package com.example.approvals;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** The approval audit trail (CMP-512): append-only, numbered from 1, timed by the injected clock. */
public final class AuditTrail {

    private final Clock clock;
    private final Map<String, List<AuditEntry>> byPayment = new HashMap<>();
    private long nextSequence = 1;

    public AuditTrail(Clock clock) {
        this.clock = clock;
    }

    public AuditEntry record(String paymentId, Action action, List<String> approvers) {
        AuditEntry entry = new AuditEntry(nextSequence++, paymentId, action, clock.instant(), approvers);
        byPayment.computeIfAbsent(paymentId, id -> new ArrayList<>()).add(entry);
        return entry;
    }

    /** The payment's trail so far, oldest first. */
    public List<AuditEntry> entries(String paymentId) {
        return Collections.unmodifiableList(byPayment.getOrDefault(paymentId, List.of()));
    }

    public long size() {
        return nextSequence - 1;
    }
}
