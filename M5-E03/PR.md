# Status history for payment accounts

Compliance needs a record of every freeze, unfreeze and closure of a
payment account, who did it and when (CMP-412). This adds a status
history and the hourly feed to the compliance archive.

## Requirements

- Every successful status change records the time, the account, the old
  and new status and the actor. A rejected transition records nothing.
- Timestamps are timezone-aware UTC, like every other event in the
  compliance archive, which merges them by time with events from other
  systems.
- The history is an audit record: nothing a caller does with what
  `history()` returns may change or remove recorded changes.
- `changes_since(since)` returns the changes at or after `since`. The
  archive feed passes `since` as an aware UTC datetime.
- The existing lifecycle rules are unchanged: a closed account cannot be
  reopened.

## Changes

- `change_status` takes an `actor` and records a `StatusChange`.
- New `history(account_id=None)` and `changes_since(since)`.
- Tests for a recorded freeze, ordering across accounts, the closed rule
  and `changes_since`.
