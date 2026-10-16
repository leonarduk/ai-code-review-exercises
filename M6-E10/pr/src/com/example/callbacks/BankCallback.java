package com.example.callbacks;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

/** One payment callback from the bank, with the signature the gateway verified. */
public record BankCallback(String callbackId, String paymentId, String status, BigDecimal amount, byte[] signature) {

    public BankCallback {
        signature = signature.clone();
    }

    /** Parses a body of the form {@code callbackId|paymentId|status|amount}. */
    public static BankCallback parse(String body, byte[] signature) {
        String[] f = body.split("\\|", -1);
        if (f.length != 4 || Arrays.stream(f).anyMatch(String::isEmpty)) {
            throw new IllegalArgumentException("malformed callback body: " + body);
        }
        return new BankCallback(f[0], f[1], f[2], new BigDecimal(f[3]), signature);
    }

    @Override
    public byte[] signature() {
        return signature.clone();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BankCallback c
                && callbackId.equals(c.callbackId)
                && paymentId.equals(c.paymentId)
                && status.equals(c.status)
                && amount.equals(c.amount)
                && Arrays.equals(signature, c.signature);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(callbackId, paymentId, status, amount) + Arrays.hashCode(signature);
    }
}
