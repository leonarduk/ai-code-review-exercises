# Merchant discount and net settlement per batch

Merchants are paid gross today and invoiced for the merchant discount at
month end; the scheme now requires the discount to be netted from each daily
settlement (ACQ-2291). This adds the fee per transaction and the net
amount to `SettlementBatch`.

## Requirements

- The merchant discount rate (MDR) is agreed per merchant in basis points,
  0 to 1,000 inclusive. Any other rate is rejected with
  `IllegalArgumentException`.
- Fee on a transaction = amount x MDR / 10,000, in the batch currency's
  minor units, rounded to the minor unit half away from zero.
- A refund's fee is the exact negative of the fee on a sale of the same
  amount, so a sale refunded in full costs the merchant nothing.
- Batch fees = the sum of the transaction fees. Net = gross minus fees, and
  is negative when refunds exceed sales.
- Batches settle in EUR, GBP, USD, JPY and KRW. Transaction amounts are
  validated upstream to at most 999,999,999,999 minor units, and a large
  merchant's daily KRW batch runs to hundreds of billions of won. Fees and
  net stay exact, as the batch's gross already does.

## Changes

- `SettlementBatch` takes the merchant's MDR; new `feeMinor`, `feesMinor`
  and `netMinor`.
- Tests for sale and refund fees, netting, an empty batch and the MDR
  range.
