package com.example.callbacks;

/** POST /callbacks/payments. The gateway has already verified the signature. */
public final class CallbackEndpoint {

    private final CallbackInbox inbox;

    public CallbackEndpoint(PaymentOutcomes outcomes) {
        this.inbox = new CallbackInbox(outcomes);
    }

    /** Records the outcome in the body {@code callbackId|paymentId|status|amount} once and returns the HTTP status. */
    public int post(String body, byte[] signature) {
        BankCallback callback;
        try {
            callback = BankCallback.parse(body, signature);
        } catch (IllegalArgumentException e) {
            return 400;
        }
        return switch (inbox.receive(callback)) {
            case ACCEPTED, DUPLICATE -> 200;
            case CONFLICT -> 409;
        };
    }
}
