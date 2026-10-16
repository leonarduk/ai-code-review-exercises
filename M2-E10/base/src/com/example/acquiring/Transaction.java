package com.example.acquiring;

/**
 * A card transaction in the batch currency's minor units (cents, yen,
 * won): positive for a sale, negative for a refund.
 */
public record Transaction(String id, long amountMinor) {
}
