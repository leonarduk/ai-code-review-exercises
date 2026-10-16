# Coupon schedule for fixed-rate bonds

Bond servicing needs the list of coupon payments for each fixed-rate bond
we hold (BND-640). This adds an unadjusted schedule; business-day
adjustment of payment dates comes in a later PR.

## Requirements

- Coupon dates are the first coupon date plus k x (12 / frequency)
  months, for k = 0, 1, 2, ..., up to and including the maturity date,
  which is always the final coupon date. (Maturity is always on the
  schedule; the bond master validates that before we see the bond.)
- Every date is calculated from the first coupon date, so a bond whose
  first coupon is on the 31st pays on the 31st in every month that has
  one, and on the last day of shorter months.
- The frequency must divide 12 (1, 2, 3, 4, 6 or 12); anything else is
  rejected. A first coupon date after maturity is rejected.
- Every coupon pays notional x annual rate / frequency, rounded half-even
  to the cent.

## Changes

- New `Coupon` record and `CouponSchedule` with `dates` and `coupons`.
- Tests for semi-annual dates, a quarterly amount, an annual schedule and
  frequency validation.
