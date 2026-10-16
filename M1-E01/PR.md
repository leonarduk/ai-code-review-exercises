# Introduce tiered custody fees

Replaces the flat 0.25% custody fee with the tiered schedule agreed with
Finance (ticket FIN-2291).

## Requirements (from the fee schedule)

- The whole holding value is charged at the rate of the tier it falls into
  (not marginal bands).
- Tiers, with inclusive upper limits:
  - up to and including 100,000.00: 0.30%
  - up to and including 1,000,000.00: 0.20%
  - above 1,000,000.00: 0.10%
- Fees are rounded to the cent with banker's rounding (ROUND_HALF_EVEN),
  consistent with the ledger.
- Minimum annual fee 25.00, which also applies to empty accounts.
- Negative holding values are rejected.

## Changes

- `custody_fees.py`: new `FeeTier` / `FeeSchedule` types, loaded from
  `fee_tiers.json` so Finance can change rates without a code change.
  `annual_fee()` takes an optional schedule (defaults to the JSON file).
- `fee_tiers.json`: the schedule above.
- Tests rewritten around the tiered schedule.

All tests pass locally.
