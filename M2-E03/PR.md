# Allocate platform fees pro rata across client accounts

The monthly platform fee is booked to the master account only; client
reporting needs it split across sub-accounts (CLIENT-772).

## Requirements

- `FeeAllocator.allocate(total, balances)` splits `total` across accounts
  in proportion to their balances: each account's share is its exact
  proportion of the total, rounded to the cent with HALF_EVEN.
- The allocated amounts must sum exactly to `total`: the rounding residue
  (a cent or two, positive or negative) goes to the account with the largest
  balance, the first such account on a tie.
- The result keeps the input map's iteration order.
- `total` must be whole cents. Reject it, an empty account set, negative
  balances and an all-zero balance set with `AllocationException`.

Balances are carried in cents (scale 2) by the positions service.

## Changes

- New `FeeAllocator`, `Allocation` and `AllocationException`.
- Tests for proportionality, summing to the total and validation.
