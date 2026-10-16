# Modified Following adjustment for payment schedules

Swap and bond payment schedules are generated unadjusted and need rolling to
business days with the trade's business-day convention (DERIV-512).

## Requirements

- Support the ISDA conventions Following, Preceding and Modified Following.
- Modified Following: roll forward to the next business day, unless that
  date falls in a different calendar month from the unadjusted date, in
  which case roll back to the previous business day instead.
- Business days are weekdays not in the supplied holiday set.
- `adjust_schedule` applies one convention to a whole schedule; an unknown
  convention name is rejected with `ValueError`.

## Changes

- `preceding`, `modified_following`, `adjust_schedule` and the
  `CONVENTIONS` table, next to the existing `following`.
- Tests for each convention, including month-end roll-backs.
