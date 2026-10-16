# Daily accrual schedule for term deposits

Adds a per-day accrual schedule so the interest posting job can book
accruals daily instead of at maturity (DEP-930).

## Requirements

- Day count ACT/365 Fixed: each day accrues `principal x rate / 365`,
  including in leap years.
- The accrual period is [start, end): interest accrues for the start date
  and every day up to, but not including, the end date (the maturity or
  next posting date). A period with `start == end` accrues nothing.
- Daily amounts are kept unrounded; `accrued_interest` rounds the period
  total once, to the cent, with ROUND_HALF_EVEN.
- `end` before `start` raises `ValueError`.

## Changes

- `accrual.py`: `DailyAccrual`, `daily_accruals()` and `accrued_interest()`;
  `simple_interest()` unchanged apart from using the shared constant.
- Tests for the schedule and the period total.
