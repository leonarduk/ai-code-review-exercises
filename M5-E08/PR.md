# Validate journal entries before posting

The general ledger accepts any journal entry, and an unbalanced one
reached the trial balance last month (GL-718). This validates entries
before they are posted.

## Requirements

- An entry has at least two lines. Every amount is positive; the side
  (debit or credit) gives its direction.
- An entry balances when, in every currency it uses, total debits equal
  total credits. Entries may use several currencies (an FX trade posts
  through an FX position account in each currency), but amounts in
  different currencies never offset each other.
- An unbalanced entry is rejected with `UnbalancedEntry` and is not
  posted.

## Changes

- New `validate(entry)`, called by `GeneralLedger.post`.
- Tests for a balanced and an unbalanced entry, an FX trade, and the line
  rules.
