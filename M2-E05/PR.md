# Interest and maturity amount for money-market deposits

Treasury books USD and EUR money-market deposits from overnight to twelve
months; confirmation letters need the interest and maturity amount
(TREAS-455).

## Requirements

- Day count ACT/360: interest = principal x rate x days / 360, where
  days is the actual number of days in [value date, maturity date).
- Interest is rounded once to the cent, HALF_EVEN. The maturity amount is
  principal plus that rounded interest.
- Negative rates are valid and produce negative interest.
- Principal must be positive; maturity must be after the value date.

## Changes

- `Deposit` gains validation, `days()`, `interest()` and
  `maturityAmount()`.
- Tests for overnight and one-week deposits and validation.
