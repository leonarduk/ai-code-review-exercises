package com.example.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class PaymentHandlerTest {

    private final AtomicInteger submitted = new AtomicInteger();
    private final PaymentHandler handler = new PaymentHandler(r -> "PAY-" + submitted.incrementAndGet());

    private static PaymentRequest request(String amount) {
        return new PaymentRequest("ACC-77", "GB33BUKB20201555555555", new BigDecimal(amount), "GBP");
    }

    @Test
    void theFirstRequestIsSubmitted() {
        assertEquals(new PaymentResponse("PAY-1", "ACCEPTED"), handler.handle("k-1", request("100.50")));
        assertEquals(1, submitted.get());
    }

    @Test
    void aRetryGetsTheOriginalResponse() {
        PaymentResponse first = handler.handle("k-1", request("100.50"));
        assertEquals(first, handler.handle("k-1", request("100.50")));
        assertEquals(1, submitted.get());
    }

    @Test
    void differentKeysAreDifferentPayments() {
        handler.handle("k-1", request("100.50"));
        assertEquals("PAY-2", handler.handle("k-2", request("100.50")).paymentId());
    }

    @Test
    void aDifferentPaymentUnderTheSameKeyIsAConflict() {
        handler.handle("k-1", request("100.50"));
        assertThrows(IdempotencyConflictException.class, () -> handler.handle("k-1", request("200.00")));
    }

    @Test
    void theKeyIsRequired() {
        assertThrows(IllegalArgumentException.class, () -> handler.handle(null, request("100.50")));
    }
}
