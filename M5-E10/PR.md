# Feed the payments view from the provider's event stream

The payments view is updated today by a nightly batch, so the settlement
run works from yesterday's statuses (PAY-2417). This feeds it from the
card provider's event stream instead, as events arrive.

## Requirements

- Each event body is JSON with `payment_id`, `sequence`, `type` and
  `amount`. A payment's events are numbered from 1 by `sequence`, with no
  gaps. Amounts are JSON numbers with at most two decimal places and are
  read exactly, like every other amount in the view.
- The stream is partitioned by merchant and redelivers on failover, so a
  payment's events can arrive in any order and more than once. Each
  payment's events are applied in sequence order, whatever order they
  arrive in: an event that arrives before an earlier one of the same
  payment is held until every earlier one has been applied.
- An event that has already been applied, or is already held, is a
  redelivery and is not applied again.
- `status` is the type of the payment's latest event in sequence order;
  `net_captured` is captured less refunded, as today. The settlement run
  pays the merchant the `net_captured` of every payment whose status is
  `captured` or `refunded`; an `authorised` payment is not paid yet.
- `apply` keeps its signature and behaviour for the batch import.

## Changes

- New `PaymentEvent.from_json` and `PaymentProjection.receive`.
- Tests for events in order, a redelivery, an early event and a partial
  refund.
