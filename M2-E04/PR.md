# Cross-currency conversion via USD

Adds `CrossRateConverter` so settlement can convert between any two
currencies using the USD quotes from the market-data feed (SETT-1203).

## Requirements

- Quotes are in market convention `BASE/QUOTE` with USD on one side:
  `EUR/USD 1.0850` (1 EUR = 1.0850 USD), `USD/JPY 151.20` (1 USD = 151.20
  JPY). Any other pair is rejected with `IllegalArgumentException`.
- Conversions between two non-USD currencies cross through USD.
- No intermediate loss of precision: rates and intermediate amounts
  carry at least 34 significant digits (`MathContext.DECIMAL128`). Only the
  final amount is rounded, to the target currency's ISO 4217 minor units,
  HALF_EVEN.
- An unquoted currency raises `IllegalArgumentException`.

Settlement amounts reach the hundreds of millions.

## Changes

- New `CrossRateConverter`, built from the feed's quote map.
- Tests for direct and cross conversion and pair validation.
