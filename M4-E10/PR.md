# Read DepositsPlus deposits into the treasury system

Money-market deposits are still booked in DepositsPlus, and treasury
re-keys them by hand (TRY-912). This adds an adapter between the vendor's
`LegacyDepositRecord` and our `Deposit`, in both directions, so the
nightly job can load them and write maturity amendments back.

## Requirements

- DepositsPlus holds each value date as a `java.util.Date` at midnight UTC
  of that date (see `LegacyDepositRecord`). A value date read from
  DepositsPlus, or written back to it, must be the same calendar date on
  any host, whatever its default time zone. The nightly job runs on hosts
  in several regions.
- The booking timestamp is an instant and is carried over unchanged.
- Tenor: a deposit whose maturity is its start date plus a whole number
  of months n has tenor `nM` (`1M`, `3M`, `12M`). Months are added as
  `LocalDate.plusMonths` adds them: when the start's day of the month does
  not exist in the maturity month, the maturity is that month's last day.
- Any other deposit is broken-dated, and its tenor is its length in days,
  `nD`: from 15 January to 1 March is `45D`.

## Changes

- New `LegacyDepositAdapter` with `fromLegacy`, `toLegacy`, the value
  date conversions and `tenor`.
- Tests for a mapped record, the round trip, whole-month tenors and
  broken dates. The tests pin the default time zone to UTC, like the build
  agents.
