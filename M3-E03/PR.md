# Apply the client-order cut-off in New York time

The daily cut-off is 17:00 New York time, but `booking_date` compares
against a fixed 22:00 UTC, which is an hour late for half the year
(OPS-1960). This moves the comparison to New York local time.

## Requirements

- An order received before 17:00 New York time is booked on that New York
  business day; at or after 17:00, on the next business day. An order
  received on a weekend or holiday is booked on the next business day.
- New York time means local time as observed, EST in winter and EDT in
  summer.
- `received_at` must be timezone-aware; naive datetimes are rejected.
- The order feed writes timestamps in UTC without an offset
  (`2026-01-15 22:30:00`). `parse_feed_timestamp` must return them as UTC
  on any server, whatever its local time zone.

## Changes

- `booking_date` converts to New York time and applies the 17:00 cut-off,
  skipping weekends and the supplied holidays.
- New `parse_feed_timestamp`.
- Tests for orders either side of the cut-off, Friday evening and holidays.
