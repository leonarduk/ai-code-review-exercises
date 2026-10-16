package com.example.batch;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** The pain.001 group header: message id, creation time, number of transactions and control sum (PAY-1184). */
public record GroupHeader(String messageId, LocalDateTime createdAt, String currency, List<CreditTransfer> transfers) {

    private static final DateTimeFormatter ID_STAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter CREATED = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public GroupHeader {
        transfers = List.copyOf(transfers);
    }

    public static GroupHeader of(String currency, List<CreditTransfer> transfers, Clock clock, int sequence) {
        if (transfers.isEmpty()) {
            throw new IllegalArgumentException("a batch needs at least one transfer");
        }
        if (sequence < 1 || sequence > 9999) {
            throw new IllegalArgumentException("sequence must be 1 to 9999: " + sequence);
        }
        for (CreditTransfer t : transfers) {
            if (!t.currency().equals(currency)) {
                throw new IllegalArgumentException(t.endToEndId() + " is in " + t.currency() + ", the batch is in " + currency);
            }
            if (t.amount().signum() <= 0) {
                throw new IllegalArgumentException(t.endToEndId() + ": amount must be positive");
            }
            if (t.amount().stripTrailingZeros().scale() > 2) {
                throw new IllegalArgumentException(t.endToEndId() + ": more than two decimal places");
            }
        }
        LocalDateTime createdAt = LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC).withNano(0);
        String messageId = "MSG-" + createdAt.format(ID_STAMP) + "-" + String.format(Locale.ROOT, "%04d", sequence);
        return new GroupHeader(messageId, createdAt, currency, transfers);
    }

    public int numberOfTransactions() {
        return transfers.size();
    }

    public BigDecimal controlSum() {
        return transfers.stream().map(CreditTransfer::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** The GrpHdr line of the flat file: MsgId|CreDtTm|NbOfTxs|CtrlSum|Ccy. */
    public String line() {
        return String.join("|", messageId, createdAt.format(CREATED), Integer.toString(numberOfTransactions()),
                String.format(Locale.ROOT, "%.2f", controlSum()), currency);
    }
}
