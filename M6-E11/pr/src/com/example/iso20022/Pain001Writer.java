package com.example.iso20022;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Writes a customer's transfers as one ISO 20022 pain.001 message for the bank (PAY-1477). */
public final class Pain001Writer {

    private Pain001Writer() {
    }

    public static String write(String messageId, String debtorName, String debtorIban, List<Transfer> transfers) {
        if (transfers.isEmpty()) {
            throw new IllegalArgumentException("a message needs at least one transfer");
        }
        Map<LocalDate, List<Transfer>> byDate = transfers.stream()
                .collect(Collectors.groupingBy(Transfer::executionDate));
        BigDecimal controlSum = transfers.stream().map(Transfer::amount).reduce(BigDecimal.ZERO, BigDecimal::add);

        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.09\"><CstmrCdtTrfInitn>\n")
                .append("<GrpHdr><MsgId>").append(escape(messageId)).append("</MsgId>")
                .append("<NbOfTxs>").append(transfers.size()).append("</NbOfTxs>")
                .append("<CtrlSum>").append(amount(controlSum)).append("</CtrlSum></GrpHdr>\n");
        for (Map.Entry<LocalDate, List<Transfer>> block : byDate.entrySet()) {
            xml.append("<PmtInf><ReqdExctnDt><Dt>").append(block.getKey()).append("</Dt></ReqdExctnDt>")
                    .append("<Dbtr><Nm>").append(escape(debtorName)).append("</Nm></Dbtr>")
                    .append("<DbtrAcct><Id><IBAN>").append(debtorIban).append("</IBAN></Id></DbtrAcct>\n");
            for (Transfer t : block.getValue()) {
                xml.append("<CdtTrfTxInf><PmtId><EndToEndId>").append(escape(t.endToEndId())).append("</EndToEndId></PmtId>")
                        .append("<Amt><InstdAmt Ccy=\"EUR\">").append(amount(t.amount())).append("</InstdAmt></Amt>")
                        .append("<Cdtr><Nm>").append(escape(t.creditorName())).append("</Nm></Cdtr>")
                        .append("<CdtrAcct><Id><IBAN>").append(t.creditorIban()).append("</IBAN></Id></CdtrAcct>")
                        .append("<RmtInf><Ustrd>").append(t.remittanceInfo()).append("</Ustrd></RmtInf>")
                        .append("</CdtTrfTxInf>\n");
            }
            xml.append("</PmtInf>\n");
        }
        return xml.append("</CstmrCdtTrfInitn></Document>\n").toString();
    }

    /** Element text as XML requires it. */
    static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /** Exactly two decimals, never in exponent form; more than two decimals is rejected. */
    static String amount(BigDecimal value) {
        return value.setScale(2, RoundingMode.UNNECESSARY).toPlainString();
    }
}
