# Expiry dates and listed series for monthly index options

The listed-derivatives screen needs the expiry date and contract code of
each monthly index option series, and the series listed on a date
(LST-233). This adds an expiry calendar built from the exchange's holiday
list.

## Requirements

- A monthly series expires on the last Thursday of its contract month.
  When that Thursday is not a trading day, it expires on the last trading
  day before it.
- A trading day is a Monday to Friday that is not an exchange holiday.
- The contract code is the month's three-letter English abbreviation in
  capitals followed by the two-digit year, e.g. `JUN26`. It is the same on
  every host, whatever its default locale.
- The series listed on a date are the contract month of the next expiry
  on or after that date and the two months after it. A series is still
  listed on its expiry day; it trades until that day's close.

## Changes

- New `ExpiryCalendar` with `expiry`, `isTradingDay`, `code` and `listed`.
- Tests for ordinary expiries, a holiday Thursday, trading days, a code and
  the series listed mid-month.
