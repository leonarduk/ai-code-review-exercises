package com.example.callbacks;

import java.math.BigDecimal;

/** Where payment outcomes reported by the bank are recorded. */
public interface PaymentOutcomes {

    void record(String paymentId, String status, BigDecimal amount);
}
