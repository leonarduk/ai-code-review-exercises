# One trade-date rule for every confirmation source

Confirmations for the Tokyo desk come from three places, and each consumer
works out the trade date its own way (CONF-57). This puts the rule, and the
late-confirmation flag, in one module.

## Requirements

- `from_epoch_millis(ms)`: the venue's REST API sends timestamps as integer
  milliseconds since the Unix epoch. They are returned as timezone-aware UTC
  datetimes, keeping the milliseconds.
- `trade_date(value)` returns the desk's trade date as a `date`:
  - a `date` from the booking system is already a trade date and is
    returned unchanged;
  - a `datetime` from the FIX gateway is converted to Asia/Tokyo and its
    date is taken. FIX timestamps must be timezone-aware; naive ones are
    rejected with `ValueError`;
  - an `int` is a venue epoch-millisecond timestamp, dated in Asia/Tokyo.
- `is_late(executed_ms, received_ms)`: a confirmation is late when it is
  received more than 15 minutes after the execution. Both are venue
  epoch-millisecond timestamps.

## Changes

- New `confirmations` module with `from_epoch_millis`, `trade_date` and
  `is_late`.
- Tests for epoch conversion, booking dates, venue timestamps and the late
  flag.
