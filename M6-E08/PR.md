# Hold large payments to newly added beneficiaries

Most authorised push payment fraud goes to a payee the customer added the
same day (FRD-145). This adds a fraud hold on large payments to new
beneficiaries, and loads each customer's beneficiary book from the nightly
export instead of the old per-row lookups.

## Requirements

- A payment of more than 1,000.00 to a beneficiary added less than 24
  hours ago is HELD for a fraud review. Everything else is RELEASED: a
  payment of 1,000.00 or less, or to a beneficiary added 24 hours ago or
  more.
- A payment to an IBAN that is not in the customer's book adds it as a new
  beneficiary, with the payment's creditor name and the current time from
  the injected `Clock`, before it is screened, so a large first payment is
  held.
- A beneficiary that is already in the book is never re-added by a
  payment: its name and the time it was added stay as they are.
- `BeneficiaryBook.load` builds a book from the nightly export, whose rows
  are in the order they were written. An IBAN can appear more than once (a
  beneficiary renamed by the customer appears with the old and the new
  name); the last row for an IBAN wins.

## Changes

- New `PaymentScreening` with `screen`, and a `PaymentOrder` record.
- `BeneficiaryBook.load`.
- Tests for new and established beneficiaries, small and large payments, a
  first payment adding the beneficiary, and loading the export.
