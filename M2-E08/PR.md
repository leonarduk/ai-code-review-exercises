# Instalment plans for invoices

Lets an invoice be paid in equal monthly instalments (BILL-3402).

## Requirements

- `InstalmentPlan.split(total, count)` returns `count` amounts that sum
  exactly to `total`, each with two decimal places.
- Every instalment except the last is `total / count` rounded toward
  zero to the cent; the last absorbs the remainder. For totals smaller
  than `count` cents the leading instalments are 0.00; the billing run
  already skips zero-value instalments.
- Credit notes (negative totals) split the same way; every instalment keeps
  the total's sign.
- `total` may be passed with fewer decimal places (`100`, `1E+3`) but must
  not contain fractions of a cent; `count` must be at least 1. Both are
  rejected with `IllegalArgumentException`.

## Changes

- New `InstalmentPlan`.
- Tests for even and uneven splits and validation.
