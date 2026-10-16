package com.example.mandates;

import java.time.LocalDate;

/** A SEPA direct debit mandate: the debtor's authority for a creditor to collect from their account. */
public record Mandate(String creditorId, String reference, String debtorIban, LocalDate signedOn) {
}
