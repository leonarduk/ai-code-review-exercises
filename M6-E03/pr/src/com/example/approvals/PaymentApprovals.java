package com.example.approvals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Four-eyes approval of high-value payments: two different approvers release a payment. */
public final class PaymentApprovals {

    private final Map<String, List<String>> approvers = new HashMap<>();
    private final AuditTrail audit;

    public PaymentApprovals(AuditTrail audit) {
        this.audit = audit;
    }

    /** Records an approval; returns true when the payment now has two approvers and is released. */
    public boolean approve(String paymentId, String approver) {
        List<String> sofar = approvers.computeIfAbsent(paymentId, id -> new ArrayList<>());
        if (sofar.contains(approver)) {
            throw new IllegalStateException(approver + " has already approved " + paymentId);
        }
        sofar.add(approver);
        audit.record(paymentId, Action.APPROVED, sofar);
        if (sofar.size() == 2) {
            audit.record(paymentId, Action.RELEASED, sofar);
            return true;
        }
        return false;
    }
}
