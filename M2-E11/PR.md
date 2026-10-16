# Import UK-formatted statement amounts

The bank is changing its statement export to write amounts with thousands
separators (RECON-930). `new BigDecimal` rejects them, so this parses
amounts with the UK number format instead.

## Requirements

- The export's amounts are UK-formatted: an optional leading minus for
  debits, the pounds grouped in threes with commas, a decimal point and
  two decimal places. The export always writes the pence.
- Imported amounts are exact decimals with the two decimal places the
  statement shows. Reconciliation matches them against ledger postings with
  `equals`, which compares scale.
- Any amount that is not in that format fails the line with
  `StatementFormatException`, and so the whole file, as malformed amounts
  do today.
- Dates and the field layout are unchanged.

## Changes

- `StatementImporter.parseAmount` uses `NumberFormat` for `Locale.UK`.
- Tests for thousands separators, debits, a whole line and unparseable
  amounts.
