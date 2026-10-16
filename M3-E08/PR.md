# Schedule the end-of-day position snapshot

The data warehouse needs the UTC instant of each business day's end-of-day
position snapshot, and the latest snapshot as of any moment, for
point-in-time queries (DATA-118).

## Requirements

- The snapshot is taken at 17:00 New York local time on each New York
  business day (weekdays not in the supplied holiday set), whatever the
  UTC offset that day.
- `snapshot_instant(business_date)` returns that instant in UTC; a
  non-business day is rejected with `ValueError`.
- `latest_snapshot(moment)` returns the most recent snapshot instant at or
  before an aware `moment` in any time zone; a naive `moment` is rejected.

## Changes

- New `snapshots` module with `snapshot_instant` and `latest_snapshot`.
- Tests for the winter instant, before and after the close, and weekends.
