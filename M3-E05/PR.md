# Generate monthly repayment schedules

Repayment schedules for amortising loans are keyed in by hand, which keeps
producing missed and doubled months (LOAN-240). This generates them.

## Requirements

- `monthly_schedule(first_payment, count)` returns `count` payment dates,
  one per calendar month, starting with `first_payment`.
- Each payment falls on the first payment's day of the month. In a month
  that is too short for that day, it falls on the month's last day (a 30th
  payment day gives 28 or 29 February).
- End-of-month rule: if the first payment is on the last day of its month,
  every payment is on the last day of its month.
- `count` of zero gives an empty schedule; a negative count is rejected with
  `ValueError`.

## Changes

- New `schedule.monthly_schedule` and `is_month_end`.
- Tests for mid-month and month-end schedules and the count edge cases.
