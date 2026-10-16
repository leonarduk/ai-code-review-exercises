# Hold payments that repeat one from another channel

Last month a supplier was paid twice: the same instruction came in through
the payments file and, an hour later, through the API (OPS-2741). This adds
a duplicate check that runs on the intake queue before release.

## Requirements

- Two instructions are the same payment when they have the same debtor
  IBAN, creditor IBAN, currency, execution date and amount. The channel and
  the instruction id play no part.
- IBANs are compared without spaces and ignoring letter case: the branch
  channel sends them in lower case, the file channel in groups of four.
- Amounts are compared by value; the channels do not all format amounts
  the same way.
- The first instruction for a payment is released. Every later one is
  reported as a suspected duplicate, in input order, with its channel and
  the instruction id of the first, and is held.

## Changes

- New `DuplicateCheck` with `check` (the suspected duplicates) and
  `releasable` (the queue without them), and a `SuspectedDuplicate` record.
- Tests for two channels, IBAN spacing and case, a different execution date,
  repeated duplicates and the releasable list.
