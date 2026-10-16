# Apply the scheme's status reports to outgoing payments

Outgoing payments stay SUBMITTED until operations update them by hand from
the scheme's pain.002 status reports, and the customer's reserved funds sit
there until they do (PAY-1419). This applies each report as it arrives.

## Requirements

- ACCP (accepted for processing) and PDNG (pending) change nothing.
- ACSP (accepted, settlement in process) moves the payment to IN_PROCESS.
  Settlement is not complete yet: nothing is debited, and the scheme can
  still reject the payment.
- ACSC (settlement completed) moves the payment to SETTLED and debits the
  reserved funds.
- RJCT moves the payment to REJECTED and releases the reserved funds to the
  customer.
- SETTLED and REJECTED are final. The scheme repeats final reports, and a
  report for a payment that is already final changes nothing.
- Status codes are matched ignoring letter case: participant banks do not
  all send them the same way. Surrounding spaces are ignored.
- Any other code is rejected with `IllegalArgumentException` and changes
  nothing.

## Changes

- New `SchemeStatus` enum and `StatusReportHandler` with `apply`.
- Tests for a settled, a rejected and an accepted payment, a repeated final
  report and an unknown code.
