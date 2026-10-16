package com.example.beneficiaries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class PaymentScreeningTest {

    private static final Instant NOW = Instant.parse("2026-10-15T09:00:00Z");
    private static final String IBAN = "GB33BUKB20201555555555";

    private final BeneficiaryBook book = new BeneficiaryBook();
    private final PaymentScreening screening = new PaymentScreening(book, Clock.fixed(NOW, ZoneOffset.UTC));

    private static PaymentOrder order(String amount) {
        return new PaymentOrder(IBAN, "Acme Supplies Ltd", new BigDecimal(amount));
    }

    @Test
    void aLargePaymentToANewBeneficiaryIsHeld() {
        assertEquals(PaymentScreening.Decision.HELD, screening.screen(order("5000.00")));
    }

    @Test
    void aSmallPaymentToANewBeneficiaryIsReleased() {
        assertEquals(PaymentScreening.Decision.RELEASED, screening.screen(order("250.00")));
    }

    @Test
    void aLargePaymentToAnEstablishedBeneficiaryIsReleased() {
        book.add(IBAN, "Acme Supplies Ltd", NOW.minus(Duration.ofDays(30)));
        assertEquals(PaymentScreening.Decision.RELEASED, screening.screen(order("5000.00")));
    }

    @Test
    void aFirstPaymentAddsTheBeneficiary() {
        screening.screen(order("250.00"));
        assertEquals(new Beneficiary(IBAN, "Acme Supplies Ltd", NOW), book.find(IBAN).orElseThrow());
    }

    @Test
    void theNightlyExportLoadsEveryBeneficiary() {
        BeneficiaryBook loaded = BeneficiaryBook.load(List.of(
                new Beneficiary(IBAN, "Acme Supplies Ltd", Instant.parse("2025-03-01T10:00:00Z")),
                new Beneficiary("GB94BARC10201530093459", "J Smith", Instant.parse("2026-10-14T18:00:00Z"))));
        assertEquals(2, loaded.size());
        assertTrue(loaded.find("GB94BARC10201530093459").isPresent());
    }
}
