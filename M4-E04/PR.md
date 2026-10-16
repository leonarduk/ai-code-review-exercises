# Overnight financing charge on leveraged positions

Clients holding leveraged positions overnight pay a financing charge
(FIN-1184). This adds the night count and the charge.

## Requirements

- One night is charged for every London calendar day from the open date
  to the close date: opened Monday and closed Tuesday is one night, opened
  Friday and closed Monday is three. Weekends and holidays are charged
  like any other night.
- Charge = notional x annual rate x nights / basis, where the basis is
  365 for GBP and JPY and 360 for USD, EUR and CHF. Any other currency is
  rejected.
- The charge is rounded once, half-even, to the currency's minor unit
  (0 decimal places for JPY).
- A close date before the open date is rejected.

## Changes

- New `FinancingCharge` with `nights` and `charge`.
- Tests for a midweek night, a weekend, sterling and dollar charges, and
  validation.
