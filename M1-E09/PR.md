# VAT analysis for till receipts

Shop prices are VAT-inclusive, so the till cannot reuse `add_vat()`. The
receipt footer and the VAT return need the VAT backed out of the shelf
prices (RETAIL-614).

## Requirements

- Shelf prices are VAT-inclusive and in whole cents. A line's gross is its
  unit price times its quantity; refunded items have a negative quantity
  and net off against sales of the same VAT code.
- `extract_vat(gross, code)` backs the VAT out of a VAT-inclusive amount.
  The VAT is `gross x rate / (1 + rate)`, rounded to the cent with
  ROUND_HALF_EVEN; the net is the gross minus that rounded VAT, so net plus
  VAT always equals the gross.
- `vat_summary(lines)` returns one row per VAT code on the receipt, in the
  order S, R, Z, whatever the order of the lines. Each row's VAT is
  extracted once from the total gross of that code's lines (the receipt is
  analysed per rate, not per line or per unit).
- `total_vat(lines)` is the sum of the rows' VAT; an empty receipt has
  0.00 VAT.
- Unknown VAT codes raise `ValueError`, as `vat_rate` already does.

## Changes

- `vat.py`: `extract_vat()`, `ReceiptLine`, `VatRow`, `vat_summary()` and
  `total_vat()`. `vat_rate()` and `add_vat()` are unchanged.
- Tests for extraction at each rate, the per-code summary and its order.
