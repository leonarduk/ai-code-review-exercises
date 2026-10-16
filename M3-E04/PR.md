# Charge intraday liquidity per hour of the London day

Treasury charges overdrawn settlement accounts per hour, and the clock-change
days are 23 and 25 hours long. The current per-day charge over-charges in
March and under-charges in October (TREAS-702).

## Requirements

- The charge for a London calendar day is
  average overdraft x annual rate x hours in that day / 8,760.
- Hours in the day means elapsed time between local midnights in
  Europe/London: 23 on the spring clock change, 25 in autumn, otherwise 24.
- Rounded to the cent, HALF_EVEN. Negative overdrafts are rejected.

## Changes

- `london_day_bounds`, `hours_in_day` and an hourly `daily_charge`.
- Tests for an ordinary day, day bounds and validation.
