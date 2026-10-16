package com.example.batch;

import java.util.List;

/** The transaction lines of the flat pain.001 file: EndToEndId|CdtrIBAN|Amt|Ccy. */
public final class TransferLines {

    private TransferLines() {
    }

    public static List<String> lines(List<CreditTransfer> transfers) {
        return transfers.stream()
                .map(t -> String.join("|", t.endToEndId(), t.creditorIban(),
                        t.amount().setScale(2).toPlainString(), t.currency()))
                .toList();
    }
}
