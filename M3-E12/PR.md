# Maturity dates for money-market tenors

Deposit and FX forward maturities are typed into the booking screen by hand
from the tenor, and Operations keeps finding them a day or two out
(MM-330). This calculates them from the spot date.

## Requirements

- `maturity(spot, tenor, holidays)`: the tenor is a positive whole number
  followed by `W` (weeks), `M` (months) or `Y` (years), for example `1W`,
  `3M` or `1Y`. Anything else is rejected with `ValueError`. Spot dates are
  always business days.
- Week tenors: the spot date plus seven calendar days per week, adjusted
  with Modified Following.
- Month and year tenors (a year is 12 months): the same day of the month
  that many calendar months after spot, or the last day of the target month
  if that month is too short, adjusted with Modified Following.
- End-end rule: if spot is the last business day of its month, the maturity
  is the last business day of the target month.
- Business days are weekdays not in the supplied holiday set.

## Changes

- `parse_tenor`, `add_months`, `last_business_day`, `is_last_business_day`
  and `maturity`, next to the existing `modified_following`.
- Tests for week and month tenors, the end-end rule and tenor validation.
