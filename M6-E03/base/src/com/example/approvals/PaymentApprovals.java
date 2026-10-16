package com.example.approvals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Four-eyes approval of high-value payments: two different approvers release a payment. */
public final class PaymentApprovals {

    private final Map<String, List<String>> approvers = new HashMap<>();

    /** Records an approval; returns true when the payment now has two approvers and is released. */
    public boolean approve(String paymentId, String approver) {
        List<String> sofar = approvers.computeIfAbsent(paymentId, id -> new ArrayList<>());
        if (sofar.contains(approver)) {
            throw new IllegalStateException(approver + " has already approved " + paymentId);
        }
        sofar.add(approver);
        return sofar.size() == 2;
    }
}
