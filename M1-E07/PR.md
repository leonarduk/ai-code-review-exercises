# Interest and maturity amount for money-market deposits

Treasury books USD and EUR money-market deposits; this adds the interest
calculation the confirmation letters need (TREAS-455).

## Requirements

- Day count ACT/360: interest = principal x rate x days / 360.
- The deposit period is [value date, maturity date): the value date accrues,
  the maturity date does not.
- Interest is rounded once to the cent, ROUND_HALF_EVEN. The maturity amount
  is principal plus that rounded interest (this is the figure on the
  confirmation letter).
- Negative rates are valid (EUR deposits paid negative rates in
  2015-2022) and produce negative interest.
- Principal must be positive; maturity must be after the value date.

## Changes

- `Deposit` gains validation, `days`, `interest()` and `maturity_amount()`.
- Tests for the ACT/360 calculation, the maturity amount and validation.
