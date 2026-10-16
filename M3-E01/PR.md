# Business-day settlement dates from the market calendar

`settlement_date` currently adds one calendar day (SETT-88). This makes it
a proper T+n calculation against the market's holiday calendar, ahead of
moving some desks from T+2 to T+1.

## Requirements

- `settlement_date(trade_date, cycle_days)`: the settlement date is the
  cycle_days-th business day after the trade date (T+0 settles on the trade
  date; trade dates are always business days).
- A business day is a weekday that is not a holiday in the market calendar.
- The calendar is `market_calendar.json`, maintained by Operations, with
  each holiday as an ISO date string.
- Negative cycles are rejected with `ValueError`.

## Changes

- New `MarketCalendar`, loaded from `market_calendar.json`.
- `settlement_date` takes the settlement cycle and an optional calendar.
- Tests for T+0, T+1 and T+2, including a Friday trade.
