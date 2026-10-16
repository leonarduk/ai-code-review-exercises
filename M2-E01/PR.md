# Migrate the monthly management fee from double to BigDecimal

Audit flagged that `MonthlyFeeCalculator` computes client fees in `double`
(FIN-3150). This moves the calculation and the invoice line to `BigDecimal`.

## Requirements (unchanged business rules)

- Monthly fee = assets under management x annual rate / 12.
- Rounded to the cent with banker's rounding (HALF_EVEN), as the ledger does.
- No invoice line is raised when the fee is zero (small or empty
  accounts), so clients are not sent zero-value invoice lines.
- Negative assets under management are rejected.
- `FeeSchedule` is owned by the client agreement service and stays as it
  is: it exposes the annual rate as a `double` (e.g. `0.006` for 0.60%).

## Changes

- `MonthlyFeeCalculator`: all arithmetic in `BigDecimal`; the annual rate is
  converted once, in the constructor.
- `InvoiceLine.amount` is now a `BigDecimal`.
- Tests rewritten for `BigDecimal` results.
