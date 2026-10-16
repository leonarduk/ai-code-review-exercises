# Record each bank callback once

The bank posts a callback for every payment outcome and retries any
callback it does not get a 200 for, sometimes minutes later. Each retry is
recorded again today, and last week one rejection was recorded four times
(OPS-2803). This records each callback once.

## Requirements

- The body is `callbackId|paymentId|status|amount`: exactly four fields
  separated by a vertical bar, none of them empty, and a decimal amount.
  Any other body is malformed and the endpoint answers 400.
- Every callback carries a signature, an HMAC of its body, which the
  gateway has already verified. A retry repeats the body and the signature
  exactly.
- The first callback with an id is recorded and the endpoint answers 200.
- A later callback with the same id, the same body and the same signature
  is a duplicate: it is not recorded again, and the endpoint answers 200
  so the bank stops retrying.
- A later callback with the same id and a different body or signature is a
  conflict: it is not recorded, and the endpoint answers 409 for operations
  to look at.
- If recording an outcome fails, the callback is not kept, so the bank's
  retry is recorded.
- The HTTP layer reuses its buffers between requests, so nothing the caller
  does with a signature array after a callback is received changes what was
  kept.

## Changes

- New `BankCallback` record with `parse`, and `CallbackInbox` with
  `receive`.
- `CallbackEndpoint.post` parses the body and answers from the inbox.
- Tests for a first callback, a retry, a conflicting body and signature, a
  malformed body and the HTTP status codes.
