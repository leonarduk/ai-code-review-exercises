# Deduplicate payment-provider webhooks

The provider retries a webhook until we answer 2xx, for up to 72 hours,
and sends the same `Idempotency-Key` each time. Some settlements are
currently booked twice (PAY-2340). This makes the receiver handle each
key once.

## Requirements

- A delivery whose key was accepted within the last 72 hours (inclusive)
  is a duplicate: it is answered with `duplicate` and not handled again.
  After 72 hours the key may be forgotten.
- If the handler raises, the delivery is not recorded, so the provider's
  retry is handled.
- The signature (HMAC-SHA256 of the body, hex) is checked, in constant
  time, before the key is looked up or anything is handled. A delivery with a bad
  signature is `rejected` and reveals nothing about which keys we have
  seen.
- `received_at` must be timezone-aware.

## Changes

- `receive` takes a `Delivery` and keeps the keys seen in the last 72
  hours.
- Tests for a first delivery, a retry, an expired key and a bad
  signature.
