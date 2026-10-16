package com.example.beneficiaries;

import java.time.Instant;

/** A payee on a customer's beneficiary list, with the time it was first added. */
public record Beneficiary(String iban, String name, Instant addedAt) {
}
