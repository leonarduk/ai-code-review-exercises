# Audit trail for credit limit changes

Internal audit found that credit limit changes leave no record of who
made them or why (RISK-881). This adds an audit trail.

## Requirements

- Every change records when (a timezone-aware UTC timestamp), who, which
  account, the old and new limits, and why. A change with no actor or no
  reason is rejected.
- A rejected change (negative limit, missing actor or reason) changes
  nothing and records nothing.
- Audit entries cannot be altered once written, either directly or
  through what `audit_trail` returns.
- An account that has never had a limit set has a limit of 0; that is the
  existing behaviour and stays.
- The clock is injectable for tests.

## Changes

- `set_limit` takes `actor` and `reason` and writes an `AuditEntry`.
- New `audit_trail(account=None)`.
- Tests for an audited change, filtering, and validation.
