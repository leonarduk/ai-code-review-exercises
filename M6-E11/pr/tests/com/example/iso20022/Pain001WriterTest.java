package com.example.iso20022;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class Pain001WriterTest {

    private static final String DEBTOR_IBAN = "DE89370400440532013000";

    private static Transfer transfer(String id, String name, String amount, String date) {
        return new Transfer(id, name, "FR1420041010050500013M02606", new BigDecimal(amount), LocalDate.parse(date),
                "Invoice " + id);
    }

    private static String write(Transfer... transfers) {
        return Pain001Writer.write("MSG-1", "Muster GmbH", DEBTOR_IBAN, List.of(transfers));
    }

    @Test
    void theGroupHeaderCountsAndSumsEveryTransfer() {
        String xml = write(transfer("E2E-1", "Dupont SA", "100.50", "2026-10-15"),
                transfer("E2E-2", "Martin SARL", "20", "2026-10-15"));
        assertTrue(xml.contains("<NbOfTxs>2</NbOfTxs><CtrlSum>120.50</CtrlSum>"));
        assertTrue(xml.contains("<InstdAmt Ccy=\"EUR\">20.00</InstdAmt>"));
    }

    @Test
    void transfersAreGroupedByExecutionDate() {
        String xml = write(transfer("E2E-1", "Dupont SA", "100.00", "2026-10-15"),
                transfer("E2E-2", "Martin SARL", "20.00", "2026-10-16"),
                transfer("E2E-3", "Petit SAS", "5.00", "2026-10-15"));
        assertTrue(xml.indexOf("<Dt>2026-10-15</Dt>") < xml.indexOf("<Dt>2026-10-16</Dt>"));
        assertTrue(xml.indexOf("E2E-3") < xml.indexOf("<Dt>2026-10-16</Dt>"));
    }

    @Test
    void namesAreEscaped() {
        String xml = write(transfer("E2E-1", "Smith & Sons", "100.00", "2026-10-15"));
        assertTrue(xml.contains("<Nm>Smith &amp; Sons</Nm>"));
    }

    @Test
    void anEmptyMessageAndThreeDecimalsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> write());
        assertThrows(ArithmeticException.class, () -> write(transfer("E2E-1", "Dupont SA", "10.005", "2026-10-15")));
    }
}
