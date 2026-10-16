# IMM dates and the front contract for quarterly futures

The hedging screen needs the front quarterly contract for SOFR and SONIA
futures, and its IMM date (FUT-509). This adds the date rules.

## Requirements

- The IMM date of a quarterly contract month (March, June, September,
  December) is the third Wednesday of that month. Other months have no
  IMM date and are rejected.
- `nextImmDate(date)` is the first IMM date on or after `date`: on an IMM
  date itself, it is that date.
- The front contract on a date is the one whose IMM date is next. Its code
  is the month letter (H, M, U, Z) followed by the last digit of the year:
  December 2026 is `Z6`, as the exchanges list it.

## Changes

- New `ImmDates` with `immDate`, `nextImmDate` and `frontContract`.
- Tests for the 2026 IMM dates, a date mid-quarter, the front contract and
  a non-quarterly month.
