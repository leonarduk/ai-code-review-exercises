# Consume ledger posting events in sequence

Postings currently reach the ledger through a nightly batch. This adds a
consumer for the payments topic so balances update as payments happen
(LED-877). The broker delivers at least once and only redelivers events
that have not been acknowledged.

## Requirements

- Each account's events carry a sequence number, starting at 1 and going up
  by one with every event for that account. An event is applied when its
  sequence is the next one expected for its account; accounts are
  independent of each other.
- An event whose sequence has already been applied is a redelivery: it is
  not applied again, and it is acknowledged.
- An event that arrives ahead of its turn (a gap) is rejected with
  `OutOfSequenceException`, so the broker redelivers it after the events
  in front of it.
- An event is acknowledged only once it has been applied or recognised as
  a redelivery. If it is rejected, or posting it fails (for example the
  account is not open yet), it is not acknowledged and the exception
  propagates, so the broker redelivers it later.
- Busy accounts receive tens of thousands of events a day; there is no
  upper limit on a sequence number.

## Changes

- New `LedgerEvent` record, `Acknowledgement` interface,
  `OutOfSequenceException` and `LedgerEventConsumer`.
- Tests for in-order events, a redelivery, a gap, independent accounts and
  a hundred events on one account.
