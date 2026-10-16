package com.example.callbacks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class CallbackInboxTest {

    private static final String BODY = "CB-1|PAY-9|ACSC|250.00";

    private final List<String> recorded = new ArrayList<>();
    private final CallbackEndpoint endpoint = new CallbackEndpoint(
            (paymentId, status, amount) -> recorded.add(paymentId + " " + status + " " + amount));

    private static byte[] sig(String s) {
        return s.getBytes(StandardCharsets.US_ASCII);
    }

    @Test
    void theFirstCallbackIsRecorded() {
        assertEquals(200, endpoint.post(BODY, sig("a1")));
        assertEquals(List.of("PAY-9 ACSC 250.00"), recorded);
    }

    @Test
    void aRetryIsNotRecordedAgain() {
        assertEquals(200, endpoint.post(BODY, sig("a1")));
        assertEquals(200, endpoint.post(BODY, sig("a1")));
        assertEquals(List.of("PAY-9 ACSC 250.00"), recorded);
    }

    @Test
    void aDifferentBodyUnderTheSameIdIsAConflict() {
        endpoint.post(BODY, sig("a1"));
        assertEquals(409, endpoint.post("CB-1|PAY-9|RJCT|250.00", sig("a1")));
        assertEquals(1, recorded.size());
    }

    @Test
    void aDifferentSignatureUnderTheSameIdIsAConflict() {
        endpoint.post(BODY, sig("a1"));
        assertEquals(409, endpoint.post(BODY, sig("b2")));
    }

    @Test
    void aMalformedBodyIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> BankCallback.parse("CB-1|PAY-9|ACSC", sig("a1")));
        assertEquals(400, endpoint.post("CB-1|PAY-9||250.00", sig("a1")));
        assertEquals(List.of(), recorded);
    }
}
