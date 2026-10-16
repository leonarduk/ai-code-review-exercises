# Cross-currency conversion via USD

Adds `convert()` so settlement can convert between any two supported
currencies using the USD quotes we already receive from the market-data feed
(SETT-1203).

## Requirements

- Market quotes arrive in market convention `BASE/QUOTE`: the price of one
  unit of BASE in QUOTE. Every quote has USD on one side, e.g.
  `EUR/USD 1.0850` (1 EUR = 1.0850 USD) and `USD/JPY 151.20`
  (1 USD = 151.20 JPY). Any other pair is rejected with `ValueError`.
- Conversions between two non-USD currencies cross through USD.
- No intermediate rounding. Only the final amount is rounded, to the
  target currency's ISO 4217 minor units, with ROUND_HALF_EVEN.
- Converting a currency to itself returns the amount rounded the same way.
- Unsupported or unquoted currencies raise `UnknownCurrencyError`.

## Changes

- `fx.py`: `RateTable` (built from the quote feed), `convert()`,
  `round_to_minor_units()` and `UnknownCurrencyError`. Reuses the existing
  `to_usd()` helper for the USD leg.
- Tests for direct, cross and same-currency conversion.
