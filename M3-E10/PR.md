# Anniversary dates for the annual custody fee

The annual custody fee is charged on each anniversary of the account's
opening date, and the fee tier depends on how many whole years the account
has been open. Both are worked out by hand in the billing spreadsheet today
(CUST-118).

## Requirements

- `anniversary(opened, year)` is the date in `year` with the opening date's
  month and day. An account opened on 29 February has its anniversary on
  28 February in years without a 29 February.
- `completed_years(opened, on)` is the number of anniversaries from the
  first one up to and including `on`: on an anniversary itself, that year
  counts as completed. A date before the account was opened is rejected
  with `ValueError`.
- `next_anniversary(opened, after)` is the first anniversary strictly after
  `after`. The opening date itself is not an anniversary.

## Changes

- New `anniversaries` module with `anniversary`, `completed_years` and
  `next_anniversary`.
- Tests for ordinary and leap-day anniversaries, the first year and the
  next anniversary.
