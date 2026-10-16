# Collections and cancellations under direct debit mandates

Creditors present SEPA direct debit collections against our debtors'
accounts, and today nothing checks them against the mandate register, so a
collection under a cancelled mandate is paid and then has to be refunded
(DD-214). This checks every collection against the register and sets its
sequence type.

## Requirements

- A mandate is identified by the creditor id and the mandate reference.
  Creditor ids are compared exactly. Mandate references are compared
  ignoring letter case: creditors' systems do not keep the case the debtor
  signed with, and the scheme treats the references as the same.
- Registering a mandate that is already registered is rejected with
  `IllegalStateException`, and the registered mandate is unchanged.
- A debtor can cancel a mandate. A collection under a mandate that is not
  registered, or has been cancelled, is refused with `NoMandateException`.
- The first collection under a mandate has sequence type FRST and every
  later one RCUR. A mandate that is cancelled and signed again is a new
  mandate, and its first collection is FRST again.
- A collection is for a positive amount.

## Changes

- New `MandateKey`, `DirectDebit` and `NoMandateException`.
- `MandateRegistry` is keyed by `MandateKey`, with new `cancel` and
  `collect`.
- Tests for registering, a duplicate, first and later collections, a
  cancelled mandate and an unknown one.
