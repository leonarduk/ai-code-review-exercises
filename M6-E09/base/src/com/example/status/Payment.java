package com.example.status;

import java.math.BigDecimal;

/** An outgoing payment. Its amount was reserved on the debtor's account when it was submitted. */
public final class Payment {

    public enum State {
        SUBMITTED,
        IN_PROCESS,
        SETTLED,
        REJECTED
    }

    private final String id;
    private final String debtorAccount;
    private final BigDecimal amount;
    private State state = State.SUBMITTED;

    public Payment(String id, String debtorAccount, BigDecimal amount) {
        this.id = id;
        this.debtorAccount = debtorAccount;
        this.amount = amount;
    }

    public String id() {
        return id;
    }

    public String debtorAccount() {
        return debtorAccount;
    }

    public BigDecimal amount() {
        return amount;
    }

    public State state() {
        return state;
    }

    public void moveTo(State next) {
        state = next;
    }

    public boolean isFinal() {
        return state == State.SETTLED || state == State.REJECTED;
    }
}
