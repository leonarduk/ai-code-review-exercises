# Audit trail for four-eyes payment approvals

Compliance needs a record of who approved each high-value payment and when
it was released (CMP-512). Approvals are only held in memory today, and the
nightly export to the compliance archive has nothing to read.

## Requirements

- Every approval writes an APPROVED entry, and the approval that releases a
  payment (its second approver) then writes a RELEASED entry.
- An entry holds a sequence number (1, 2, 3, ... across the whole trail),
  the payment id, the action, the time from the injected `Clock`, and the
  payment's approvers at that moment, in approval order.
- A repeated approval by the same person is rejected and writes nothing.
- Entries are immutable. An entry never changes once it is written, and
  nothing a caller does with a list it passed in or got back changes one.
- `entries(paymentId)` returns that payment's trail as of the call, oldest
  first. The list cannot be modified, and entries written after the call do
  not appear in it: the archive export writes each payment's entry count
  into its header and then writes the entries from the same list.

## Changes

- New `AuditEntry` record, `Action` enum and `AuditTrail`.
- `PaymentApprovals` takes an `AuditTrail` and records each approval and
  release.
- Tests for the first approval, the release, a repeated approver, an empty
  trail and sequence numbers across payments.
