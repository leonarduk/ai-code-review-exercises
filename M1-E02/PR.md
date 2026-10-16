# Round amounts in each currency's minor units

Settlement now covers JPY and KWD, so rounding everything to two decimal
places is no longer right (TREAS-418).

## Requirements

- Supported settlement currencies and their ISO 4217 minor units:
  CHF 2, EUR 2, GBP 2, JPY 0, KWD 3, USD 2. Currency codes are
  case-insensitive.
- Any other currency code must raise `UnsupportedCurrencyError`. Upload
  validation catches `MoneyError` to reject the row and report it back to
  the client.
- Rounding is banker's rounding (ROUND_HALF_EVEN), for negative amounts
  too.
- `parse_amount` reads amounts from client upload files: dot as the decimal
  separator, no thousands separators. Anything unparseable or non-finite
  raises `InvalidAmountError`.
- `format_amount` renders thousands separators and the currency's minor
  units, e.g. `1,234,567.891 KWD`.

## Changes

- `money.py`: `round_cents` replaced by `round_amount(amount, currency)`,
  plus `parse_amount`, `format_amount` and a `MoneyError` hierarchy.
- Tests cover each currency's rounding, parsing and formatting.
