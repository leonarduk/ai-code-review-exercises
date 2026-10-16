# End-of-day settlement summary per merchant

Operations build the end-of-day settlement summary in a spreadsheet from
the acquirer's GBP file (OPS-2450). This builds it from the parsed file,
together with the copy that treasury pays out from.

## Requirements

- `summarise` takes the rows of the day's file, as `parse` yields them,
  and gives for each merchant the number of payments, their total, and
  the payment ids of its three largest payments by amount, largest first.
  Merchants under investigation can be excluded; they appear nowhere in
  the summary.
- Each row is counted once, for its own merchant, however many rows a
  merchant has.
- `net_of_fees` gives treasury's copy: for each merchant, the fee (the
  total times the merchant's fee rate, rounded half-even to the penny) and
  the total less that fee. The operations summary it was built from must
  not change, at any depth: operations and treasury each keep their own.
- Every merchant in the summary has a fee rate. A missing rate is an
  error, never a fee of nothing.

## Changes

- New `summary.py` with `summarise` and `net_of_fees`.
- Tests for totals and counts, the largest payments, exclusions, and the
  treasury copy.
