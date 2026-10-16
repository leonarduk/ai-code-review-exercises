package com.example.status;

/** Applies the scheme's status reports to outgoing payments (PAY-1419). */
public final class StatusReportHandler {

    private final Funds funds;

    public StatusReportHandler(Funds funds) {
        this.funds = funds;
    }

    public void apply(Payment payment, String statusCode) {
        SchemeStatus status = SchemeStatus.valueOf(statusCode.trim());
        if (payment.isFinal()) {
            return;
        }
        switch (status) {
            case ACCP:
            case PDNG:
                break;
            case ACSP:
                payment.moveTo(Payment.State.IN_PROCESS);
            case ACSC:
                payment.moveTo(Payment.State.SETTLED);
                funds.debit(payment.debtorAccount(), payment.amount());
                break;
            case RJCT:
                payment.moveTo(Payment.State.REJECTED);
                funds.release(payment.debtorAccount(), payment.amount());
                break;
        }
    }
}
