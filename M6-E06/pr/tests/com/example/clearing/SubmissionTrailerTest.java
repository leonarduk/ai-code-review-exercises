package com.example.clearing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class SubmissionTrailerTest {

    private static Payment pounds(String reference, String amount) {
        return Payment.ofPounds(reference, "200415", "38290008", new BigDecimal(amount));
    }

    @Test
    void countAndTotal() {
        SubmissionTrailer trailer = SubmissionTrailer.of(List.of(pounds("INV-1", "125.40"), pounds("INV-2", "74.60")));
        assertEquals(new SubmissionTrailer(2, 20000), trailer);
    }

    @Test
    void trailerLineIsFixedWidth() {
        assertEquals("TRL000000200000000020000", new SubmissionTrailer(2, 20000).line());
    }

    @Test
    void fileEndsWithTheTrailer() {
        List<String> file = SubmissionTrailer.file(List.of(pounds("INV-1", "1250.00")));
        assertEquals(List.of("PAY2004153829000800000125000INV-1", "TRL000000100000000125000"), file);
    }

    @Test
    void poundsAreConvertedToPence() {
        assertEquals(1999, pounds("INV-1", "19.99").amountPence());
        assertEquals(500, pounds("INV-1", "5").amountPence());
    }

    @Test
    void fractionsOfAPennyAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> pounds("INV-1", "10.005"));
    }

    @Test
    void anEmptySubmissionIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> SubmissionTrailer.of(List.of()));
    }

    @Test
    void aZeroPaymentIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> SubmissionTrailer.of(List.of(new Payment("INV-1", "200415", "38290008", 0))));
    }
}
