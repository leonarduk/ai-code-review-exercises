# Idempotency-Key for payment initiation

Mobile clients retry POST /payments when a response times out, and each
retry currently creates a new payment (API-388). This makes the endpoint
idempotent on the Idempotency-Key header.

## Requirements

- Every request must carry an Idempotency-Key; a missing or blank key is
  rejected with `IllegalArgumentException` (the API answers 400).
- The first request with a key is submitted to the payment engine, and the
  request and the response are stored under the key.
- A later request with the same key and the same payment gets the stored
  response, and nothing is submitted again. The same payment means the
  same debtor account, creditor IBAN, currency and amount; amounts are
  compared by value, because the iOS client sends `100.5` where Android
  sends `100.50`.
- A later request with the same key and a different payment is rejected
  with `IdempotencyConflictException` (422), and nothing is submitted.
- If the payment engine refuses a payment (it throws), nothing is stored,
  so the client's retry is submitted afresh.

## Changes

- `PaymentHandler.handle` takes the Idempotency-Key and stores what was
  submitted under it.
- New `IdempotencyConflictException`.
- Tests for a first request, a retry, two keys, a conflicting payment and a
  missing key.
