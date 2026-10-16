package com.example.payments;

import java.util.ArrayList;
import java.util.List;

/** Instructions waiting for release, in the order they arrived. */
public final class IntakeQueue {

    private final List<PaymentInstruction> pending = new ArrayList<>();

    public void add(PaymentInstruction instruction) {
        pending.add(instruction);
    }

    public List<PaymentInstruction> pending() {
        return List.copyOf(pending);
    }
}
