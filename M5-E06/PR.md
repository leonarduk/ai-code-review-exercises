# Reconcile bank statements against expected receipts

Operations reconcile the daily bank statement by hand (OPS-2402). This
matches statement lines against the receipts the ledger expects.

## Requirements

- Each statement line is `date,reference,amount,currency`. Its amount is
  converted to GBP with the day's rates (GBP per unit; the table always
  includes GBP at 1) and rounded half-even to the penny.
- A line matches when the ledger expects that reference with exactly that
  GBP amount. Each expected receipt can be matched once.
- Every line that is not matched is reported: unmatched statement lines by
  reference, outstanding ledger receipts by reference, and lines that
  cannot be read (wrong number of fields, bad date, bad amount, or a
  currency with no rate) in `malformed`, with their line number. No line
  is ever silently dropped. Blank lines are ignored.

## Changes

- New `reconcile.py` with `parse_line`, `reconcile` and `Report`.
- Tests for matches, an amount mismatch, unexpected and missing receipts,
  and blank lines.
