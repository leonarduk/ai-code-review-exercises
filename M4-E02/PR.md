# FX spot value date

Deals booked through the new FX order screen need a trade date and a spot
value date (FXO-318). This adds a calculator for both, using the settlement
calendars we already load per currency.

## Requirements

- Trade date: the FX day ends at 17:00 New York time. A deal executed
  before 17:00 New York time has that New York date as its trade date; at or
  after 17:00, the next day. A trade date never falls on a Saturday or
  Sunday; it moves to the Monday.
- `executedAt` comes from several sources: the order screen sends New York
  time, the ECN feeds send UTC.
- Spot date: the trade date plus two business days, counting only days that
  are business days in both currencies of the pair.
- A pair whose currency has no calendar is rejected with
  `IllegalArgumentException`.

## Changes

- New `SpotDateCalculator` with `tradeDate` and `spotDate`.
- Tests for an ordinary deal, a deal after the 17:00 roll, the Easter
  holidays, and an unknown currency.
