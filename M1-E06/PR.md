# Speed up round_money on the batch invoicing path

Profiling the month-end invoice run (BILL-3318) shows `Decimal.quantize`
in `round_money` among the hottest calls. This switches to integer-cent
arithmetic and consolidates the tests.

## Requirements

- No behaviour change. Amounts are rounded to the cent with halves away
  from zero (ROUND_HALF_UP), for charges and credit notes (negative
  amounts) alike, as the invoicing rules require.
- `round_money` always returns exactly two decimal places.

## Changes

- `round_money` computes `int(amount * 100 + 0.5)` and scales back by
  `10**-2`.
- Tests consolidated: one parametrized sample table, a whole-cents check,
  a 1,000-amount randomized check (seeded) and invoice-line totals. The
  old hand-written expected values were replaced by a reference helper so
  the samples are easy to extend.
