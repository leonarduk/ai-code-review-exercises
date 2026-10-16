# Idempotent refunds API

Merchants' integrations retry `POST /refunds` on timeouts, and some
customers have been refunded twice (MER-1033). This makes the endpoint
idempotent on the `Idempotency-Key` header.

## Requirements

- Idempotency keys belong to the merchant that sent them. Two merchants
  may use the same key value for unrelated requests, and each must be
  processed on its own.
- A retry with the same key and the same request returns the original
  response, and no second refund is made.
- The same key with a different request (another payment or amount) is
  rejected with `IdempotencyConflict` (HTTP 422). No refund is made and the
  stored response is not returned.
- A merchant can only refund its own payments; the total refunded can never
  exceed the captured amount. A rejected request is not stored, so the
  merchant can correct it and retry with the same key.

## Changes

- New `RefundService.refund(idempotency_key, request)`.
- Tests for a refund, a retry, the refundable limit and an unknown payment.
