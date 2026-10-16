# Retry gateway timeouts and dead-letter what cannot be sent

A single gateway timeout currently fails the whole payment run, and
operations restart it by hand (PAY-2388). This retries transient failures
and moves instructions that cannot be sent to a dead-letter queue for
operations to review.

## Requirements

- An instruction that fails with `TransientError` is sent again. It is
  attempted at most `max_attempts` times in total, counting the first
  attempt; the default is 5. Retrying is safe because the gateway pays
  each `instruction_id` at most once.
- Before each retry the submitter waits, doubling from one second: 1, 2,
  4, 8 seconds with the default. It does not wait after the last attempt.
- An instruction that fails with `RejectedError` is not retried.
- When an instruction is rejected, or its last allowed attempt fails, it
  is added to the dead-letter queue with the number of attempts made and
  the reason for each failure, in order.
- A dead-letter queue can be passed in so that several submitters share
  one. Otherwise each submitter starts with its own, empty queue, which
  operations review and replay per payment rail.
- The sleep function is injectable for tests.

## Changes

- `PaymentSubmitter` takes `max_attempts`, `sleep` and `dead_letters`.
- New `DeadLetter` record.
- Tests for a first-time send, retries with backoff, a rejection and an
  instruction that keeps failing.
