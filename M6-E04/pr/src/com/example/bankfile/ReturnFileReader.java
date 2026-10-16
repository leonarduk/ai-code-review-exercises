package com.example.bankfile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Reads the bank's return file: reference,status,amount,reasonCode,reasonText (PAY-1302). */
public final class ReturnFileReader {

    private static final int FIELDS = 5;

    private ReturnFileReader() {
    }

    public static PaymentReturn parse(String line) {
        String[] f = line.split(",");
        if (f.length != FIELDS) {
            throw new IllegalArgumentException("expected " + FIELDS + " fields: " + line);
        }
        PaymentReturn.Status status = switch (f[1]) {
            case "ACCP" -> PaymentReturn.Status.ACCEPTED;
            case "RJCT" -> PaymentReturn.Status.REJECTED;
            default -> throw new IllegalArgumentException("unknown status " + f[1] + ": " + line);
        };
        return new PaymentReturn(f[0], status, new BigDecimal(f[2]), f[3], f[4]);
    }

    /** Every line after the header; one malformed line fails the whole file, so it can be requested again. */
    public static List<PaymentReturn> read(List<String> lines) {
        List<PaymentReturn> returns = new ArrayList<>();
        for (String line : lines.subList(1, lines.size())) {
            returns.add(parse(line));
        }
        return returns;
    }
}
