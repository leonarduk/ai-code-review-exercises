# Statutory interest on overdue invoices

Legal asked for statutory late-payment interest on overdue B2B invoices
(AR-611). This adds the calculation; the rate comes from the finance-policy
service, which publishes it as a `double`.

## Requirements

- Simple interest, ACT/365 Fixed (365 even in leap years), from the due
  date up to but not including the payment date.
- Rounded to the cent half-up, as the statutory rules specify (not the
  ledger's half-even).
- No interest if the invoice is paid on or before the due date, or if the
  overdue amount is zero or a credit (negative), whatever its scale.
- The result always has two decimal places.

## Changes

- New `LatePaymentInterest`, built from `StatutoryRates`.
- Tests for a 30-day late payment, on-time payment and credit balances.
