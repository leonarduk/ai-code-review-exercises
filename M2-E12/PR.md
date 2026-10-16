# Calculate VAT on invoices and receipts

VAT is keyed in by hand on invoices and credit notes today (TAX-207). This
adds `VatCalculator` so billing can calculate it.

## Requirements

- VAT on a net amount = net x rate, rounded to the penny half-up, that is
  half away from zero. A credit note's VAT is the exact negative of the VAT
  on an invoice for the same net amount.
- VAT contained in a VAT-inclusive amount (till receipts) = gross x rate /
  (1 + rate), rounded to the penny half-up. It must be produced at every
  rate, including those where the VAT fraction has no exact decimal.
- An invoice's VAT is calculated line by line: each line's VAT is rounded,
  and the invoice total is the sum of the rounded line amounts, so the
  lines printed on the invoice add up to its total.
- A rate is a fraction from 0 to 1 inclusive (0.20 for the standard rate).
  Anything else, such as a negative rate or a percentage written as 20, is
  rejected with `IllegalArgumentException`.

## Changes

- New `VatCalculator` with `vatOnNet`, `vatInGross` and `invoiceVat`.
- Tests for each rate band, VAT-inclusive amounts, an invoice total and
  rate validation.
