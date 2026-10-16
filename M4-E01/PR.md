# Transaction report: trade date and trading date time fields

The daily transaction report to the regulator needs two new fields per
execution, the trade date and the trading date time (REG-2207). This adds a
formatter that renders a report row from an `Execution`.

## Requirements

- Trading date time: the instant of execution in UTC, formatted
  `2026-02-10T09:15:30.123456Z`. Precision is the microsecond; finer digits
  are truncated, never rounded.
- Trade date: the UTC calendar date of the execution, formatted `20260210`.
- `executedAt` arrives in the venue's own time zone (London, New York,
  Frankfurt, Tokyo...), so both fields must convert to UTC first.
- Quantity and price: plain decimals with no trailing zeros and no exponent
  notation (`100`, not `1E+2`).
- A row is: transaction id; ISIN; trade date; trading date time; quantity;
  price, separated by semicolons.

## Changes

- New `TransactionReportFormatter` with one method per field and `row`.
- Tests for each field and for a full row.
