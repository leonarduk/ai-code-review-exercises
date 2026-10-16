# Consume payment instructions from Kafka

Payment instructions now arrive on the `payments.instructions` topic
instead of the nightly file (PAY-2210). This adds the consumer that posts
them to the ledger.

## Requirements

- Delivery is at least once. A message can be redelivered after a
  consumer restart or rebalance, and the producer retries on timeout, so
  the same instruction can also be published again at a new offset.
- An instruction is posted at most once, however many times or at
  whichever offsets it arrives. `instruction_id` is the instruction's
  unique business key.
- If the debit account cannot cover it, `handle` re-raises
  `InsufficientFunds` so that the message is not committed and is
  redelivered; the instruction id is added to `rejected` for the
  operations alert. When it is redelivered and the funds are there by
  then, it must be posted.
- Amounts arrive as decimal strings and are posted exactly.

## Changes

- New `consumer.py` with `Message.from_record` and `PaymentConsumer.handle`.
- Tests for posting, redelivery and insufficient funds.
