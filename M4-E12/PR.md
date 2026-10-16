# ACT/ACT (ISDA) accrual for fixed swap legs

Fixed legs on the new sterling and dollar swaps are documented with the
ACT/ACT (ISDA) day count, which the accrual engine does not support yet
(IRS-1450). This adds the year fraction and the fixed-leg interest for an
accrual period.

## Requirements

- An accrual period includes its start date and excludes its end date. A
  period that ends before it starts is rejected with
  `IllegalArgumentException`; an empty period accrues nothing.
- Year fraction, ACT/ACT (ISDA): every day of the period is counted in
  the calendar year it falls in. The days that fall in a leap year are
  divided by 366, the days that fall in any other year by 365, and the
  two are added.
- Leap years follow the Gregorian calendar.
- The year fraction is kept unrounded, to 34 significant digits
  (`MathContext.DECIMAL128`).
- Interest = notional x fixed rate x year fraction, rounded once,
  half-even, to the cent. Every leg in scope is in USD, GBP or EUR.

## Changes

- New `ActActIsda` with `yearFraction` and `interest`.
- Tests for periods in a common and a leap year, a period ending on the
  last day of the year, an empty and a reversed period, and an interest
  amount.
