# Net amounts per tax rate for daily settlement files

Operations reconcile each day's settlement files against the tax ledger by
net amount per tax rate (OPS-2207). This adds the summary.

## Requirements

- Input: the file's lines, each `tradeId,netAmount,taxRate`, e.g.
  `T-1001,1250.00,0.20`. Reversals carry negative net amounts and net off.
- Output: the total net amount for each tax rate.
- Numerically equal rates are the same rate: CLR-A writes `0.20`,
  CLR-B writes `0.2`, and both must land in one total.
- A file is booked whole or not at all. Any line with the wrong number of
  fields, or whose amount or rate is not a valid decimal, fails the file
  with `SettlementFileException` so operations can correct and resubmit it.

## Changes

- New `SettlementSummary.netByTaxRate` and `SettlementFileException`.
- Tests for totalling, whitespace and field-count validation.
