# Monthly compounding projection for savings accounts

The savings illustration sent with every new account shows the balance
month by month. It must agree to the cent with what the ledger will
actually credit (SAV-208).

## Requirements

- Interest is credited monthly. Each month earns the month's opening
  balance x annual rate / 12, rounded to the cent with ROUND_HALF_EVEN,
  and that rounded credit is added to the balance, so later months earn
  interest on it. This is how the ledger posts interest; the projection
  must reproduce the ledger, not the textbook compound-interest formula.
- An optional monthly deposit is paid in after that month's interest is
  credited.
- `project_savings` returns one entry per month: the month number, the
  interest credited and the closing balance.
- `total_interest` is the sum of the monthly credits, as a Decimal in
  cents; a projection of zero months has 0.00 interest.
- `aer(rate)` is the annual equivalent rate of monthly compounding,
  `(1 + rate / 12) ** 12 - 1`, as a percentage rounded half-even to two
  decimal places.
- The opening balance and the deposit are non-negative whole cents; the
  rate and the number of months are non-negative. Anything else raises
  `ValueError`.

## Changes

- `savings.py`: `MonthlyCredit`, `project_savings()`, `total_interest()`
  and `aer()`. `annual_interest()` is unchanged.
- Tests for the first months of a projection, deposits, a year's total,
  the AER and input validation.
