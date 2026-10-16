package com.example.bankfile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class BankFileTest {

    private static OutgoingPayment pay(String amount, String currency) {
        return new OutgoingPayment("PAY-1", "DE89370400440532013000", new BigDecimal(amount), currency);
    }

    @Test
    void amountsAreWrittenWithTwoDecimals() {
        assertEquals("PAY-1,DE89370400440532013000,250.50,EUR", PaymentFileWriter.line(pay("250.5", "EUR")));
        assertEquals("PAY-1,DE89370400440532013000,1000.00,EUR", PaymentFileWriter.line(pay("1E+3", "EUR")));
    }

    @Test
    void theFileStartsWithTheHeader() {
        assertEquals(List.of("reference,iban,amount,currency", "PAY-1,DE89370400440532013000,12.00,GBP"),
                PaymentFileWriter.file(List.of(pay("12", "GBP"))));
    }

    @Test
    void onlyEurGbpAndUsdAreAccepted() {
        assertThrows(IllegalArgumentException.class, () -> PaymentFileWriter.line(pay("1500", "JPY")));
    }

    @Test
    void moreThanTwoDecimalsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> PaymentFileWriter.line(pay("10.005", "EUR")));
    }

    @Test
    void readsAcceptedAndRejectedLines() {
        List<PaymentReturn> returns = ReturnFileReader.read(List.of(
                "reference,status,amount,reasonCode,reasonText",
                "PAY-1,ACCP,250.50,ACSC,Settled",
                "PAY-2,RJCT,99.00,AC04,Account closed"));
        assertEquals(List.of(
                new PaymentReturn("PAY-1", PaymentReturn.Status.ACCEPTED, new BigDecimal("250.50"), "ACSC", "Settled"),
                new PaymentReturn("PAY-2", PaymentReturn.Status.REJECTED, new BigDecimal("99.00"), "AC04", "Account closed")),
                returns);
    }

    @Test
    void aLineWithMissingFieldsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ReturnFileReader.parse("PAY-1,ACCP,250.50"));
    }

    @Test
    void anUnknownStatusIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ReturnFileReader.parse("PAY-1,PDNG,250.50,X,Y"));
    }
}
