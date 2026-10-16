# Settlement amounts for deliverable FX forwards

Operations still work out forward settlement amounts in a spreadsheet.
This moves the calculation into `forwards.py` so the settlement
instruction can be generated directly (TREAS-512).

## Requirements

- The client pays the notional converted at the all-in forward rate,
  in the quote currency (every supported quote currency has two decimal
  places), plus the charges in their tariff.
- The converted amount is rounded once, to the cent, with ROUND_HALF_EVEN.
  No intermediate rounding: corporate and interbank notionals run into the
  billions and must still settle exact to the cent.
- A tariff has up to two charges, `broker_fee` and `clearing_fee`, in the
  quote currency and in whole non-negative cents. A tariff that omits a
  charge charges nothing for it. Any other entry is rejected with
  `ValueError`.
- Tariffs are stored as JSON with plain numbers, e.g.
  `{"broker_fee": 25.00}`; `load_tariff` must read them exactly, without
  binary floating point.
- The notional must be positive.
- Rate arithmetic must not change the process-wide decimal context, which
  other services in the same process rely on.

## Changes

- `forwards.py`: `load_tariff()`, `converted_amount()` and
  `settlement_amount()`. The rate is worked out in a local decimal context
  at the rate feed's precision.
- Tests for conversion, charges, tariff parsing and validation.
