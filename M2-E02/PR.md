# Round and format amounts in each currency's minor units

Settlement is adding JPY and KWD, so `roundCents` (always two decimal
places) is no longer enough (TREAS-418). This uses `java.util.Currency` for
the ISO 4217 minor units instead of a hand-maintained table.

## Requirements

- Settlement currencies: CHF, EUR, GBP, JPY, KWD, USD. Codes are
  case-insensitive.
- Any other code -- unknown codes and valid ISO codes we do not settle in
  -- raises `UnsupportedCurrencyException`, which upload validation reports
  back to the client.
- Rounding to the currency's minor units is half-even, for negative amounts
  too.
- `format` renders statement amounts in plain notation with the currency's
  minor units: `1250.50 USD`, `1000.00 USD`, `1250 JPY`, `1.200 KWD`.

## Changes

- `MoneyRounding.round(amount, currencyCode)` and `format(...)` replace
  `roundCents`.
- New `UnsupportedCurrencyException`.
- Tests for each currency's rounding and formatting.
