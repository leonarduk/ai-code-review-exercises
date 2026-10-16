# Allocate platform fees pro rata across client accounts

The monthly platform fee is currently booked to the master account only.
Client reporting needs it split across the sub-accounts (CLIENT-772).

## Requirements

- `allocate(total, balances)` splits `total` across accounts in proportion
  to their balances.
- Each account's share is rounded to the cent with ROUND_HALF_EVEN.
- The allocated amounts must sum exactly to `total`. Any rounding residue
  (positive or negative) goes to the account with the largest balance; on a
  tie, the first such account in input order.
- Accounts with a zero balance are allowed and receive 0.00.
- `total` must be a whole number of cents. Reject it, an empty account set,
  negative balances and an all-zero balance set with `AllocationError`.
- The result keeps the input's account order.

## Changes

- New `allocation.py` with `allocate()` and the `Allocation` result type.
- Tests for proportionality, summing to total, ordering and validation.
