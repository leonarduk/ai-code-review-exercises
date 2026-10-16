# Match counterparty confirmations against booked trades

Counterparties send an end-of-day confirmation file for every trade done
with them, and operations still match it against the blotter by hand
(OPS-2741). This adds parsing of a confirmation line and the field-by-field
match against our booked `Trade`.

## Requirements

- A confirmation line is: trade id; trade date; execution time; quantity;
  price, separated by semicolons. A line with any other number of fields is
  rejected with `IllegalArgumentException`.
- The trade date is day/month/year with a two-digit day and month and a
  four-digit year, e.g. `14/04/2026`.
- A trade date that does not exist in the calendar is malformed. Parsing
  rejects it with `DateTimeParseException`, so the line goes to the
  exceptions queue instead of being matched.
- The execution time is ISO 8601 with an offset. Each counterparty reports
  it in its own offset, which need not be the offset we booked.
- A confirmation matches a trade when the trade ids and trade dates are
  equal, the execution times are the same instant, and the quantities and
  prices are equal by value, whatever their scale.
- `mismatches` lists the fields that do not match, in line order; an empty
  list is a match.

## Changes

- New `Confirmation` record and `ConfirmationMatcher` with `parse`,
  `mismatches` and `matches`.
- Tests for parsing, a match, scale-insensitive amounts, reported
  mismatches and malformed lines.
