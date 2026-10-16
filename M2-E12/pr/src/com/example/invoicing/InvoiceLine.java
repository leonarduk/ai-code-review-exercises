package com.example.invoicing;

import java.math.BigDecimal;

/**
 * One invoice line: a net amount in pounds and pence (negative on a credit
 * note) and its VAT rate as a fraction, e.g. 0.20 for the standard rate.
 */
public record InvoiceLine(String description, BigDecimal net, BigDecimal vatRate) {
}
