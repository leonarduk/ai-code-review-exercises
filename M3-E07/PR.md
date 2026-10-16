# CSDR cash penalties for late settlement

Moves the CSDR late-settlement penalty calculation out of the Operations
spreadsheet (SETT-301).

## Requirements

- A failing instruction is penalised for each business day in
  [intended settlement date, actual settlement date): the intended date
  counts if the instruction did not settle on it, the actual settlement date
  does not.
- Business days are weekdays not in the supplied holiday set (the CSD's
  calendar, as dates).
- Penalty = instruction value x daily rate (in basis points) x business days
  late, rounded to the cent with HALF_EVEN.
- An instruction that settles on or before its intended date is never
  penalised.

## Changes

- New `penalties` module with `business_days_late` and `penalty`.
- Tests for one day late, over a weekend, on time and the amount.
