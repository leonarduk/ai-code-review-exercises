package com.example.bankfile;

import java.math.BigDecimal;

/** A payment to be sent to the bank in the daily CSV file. */
public record OutgoingPayment(String reference, String iban, BigDecimal amount, String currency) {
}
