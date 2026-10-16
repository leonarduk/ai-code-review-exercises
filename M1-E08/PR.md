# Instalment plans for invoices

Lets an invoice be paid in equal monthly instalments (BILL-3402).

## Requirements

- `split_into_instalments(total, count)` returns `count` amounts that sum
  exactly to `total`.
- Every instalment except the last is `total / count` rounded toward zero to
  the cent; the last instalment absorbs the remainder. (Customers prefer the
  odd cent at the end of the plan.) For totals smaller than `count` cents
  the leading instalments are 0.00; the billing run already skips
  zero-value instalments.
- Credit notes (negative totals) are split the same way: every instalment
  keeps the total's sign.
- Amounts may be passed as `Decimal`, `int` or `str` and must be whole
  cents. Floats (and bools) are rejected with `TypeError`; unparseable
  strings, non-finite values and fractions of a cent with `ValueError`.
- `count` must be at least 1.

## Changes

- New `instalments.py` with `to_amount()` and `split_into_instalments()`.
- Tests for even and uneven splits and input validation.
