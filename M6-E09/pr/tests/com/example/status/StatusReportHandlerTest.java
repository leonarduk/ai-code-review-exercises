package com.example.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class StatusReportHandlerTest {

    private final List<String> movements = new ArrayList<>();
    private final StatusReportHandler handler = new StatusReportHandler(new Funds() {
        @Override
        public void debit(String account, BigDecimal amount) {
            movements.add("debit " + account + " " + amount);
        }

        @Override
        public void release(String account, BigDecimal amount) {
            movements.add("release " + account + " " + amount);
        }
    });

    private final Payment payment = new Payment("PAY-1", "ACC-7", new BigDecimal("250.00"));

    @Test
    void aSettledPaymentIsDebited() {
        handler.apply(payment, "ACSC");
        assertEquals(Payment.State.SETTLED, payment.state());
        assertEquals(List.of("debit ACC-7 250.00"), movements);
    }

    @Test
    void aRejectedPaymentReleasesTheFunds() {
        handler.apply(payment, "RJCT");
        assertEquals(Payment.State.REJECTED, payment.state());
        assertEquals(List.of("release ACC-7 250.00"), movements);
    }

    @Test
    void anAcceptedPaymentIsUnchanged() {
        handler.apply(payment, "ACCP");
        assertEquals(Payment.State.SUBMITTED, payment.state());
        assertEquals(List.of(), movements);
    }

    @Test
    void aRepeatedFinalReportChangesNothing() {
        handler.apply(payment, "ACSC");
        handler.apply(payment, "ACSC");
        handler.apply(payment, "RJCT");
        assertEquals(Payment.State.SETTLED, payment.state());
        assertEquals(List.of("debit ACC-7 250.00"), movements);
    }

    @Test
    void anUnknownCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> handler.apply(payment, "ABCD"));
        assertEquals(Payment.State.SUBMITTED, payment.state());
    }
}
