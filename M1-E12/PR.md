# Per-scheme card fees, fee bounds and refunds

Merchants now pay a different rate per card scheme, with a minimum and a
maximum fee per transaction, and refunds give the fee back (MERCH-377).
`sale_fee()` stays for the legacy statement job.

## Requirements

- Rates per scheme: VISA 1.95%, MASTERCARD 1.95%, AMEX 2.90%. Scheme names
  are case-insensitive; any other scheme raises `ValueError`.
- The percentage fee is truncated toward zero to the cent, as `sale_fee`
  already does, so the merchant is never charged a fraction of a cent.
- Every fee is at least 0.20 and at most 15.00 in size.
- A refund is a transaction with a negative amount. Its fee is refunded to
  the merchant in full and by the same rules: the fee on a refund of any
  amount is exactly the negative of the fee on a sale of that amount,
  minimum and maximum included. A sale refunded in full therefore nets to
  zero fees.
- A zero amount raises `ValueError`.
- `fees_due` is the net of the fees on a settlement batch.

## Changes

- `merchant_fees.py`: `SCHEME_RATES`, `scheme_rate()`, `transaction_fee()`,
  `Transaction` and `fees_due()`. `sale_fee()` is unchanged.
- Tests for each scheme, both fee bounds, refunds and a netted batch.
