# Waive the maintenance fee on average daily balance

Customers avoid the monthly maintenance fee by moving money in on the last
day of the statement period and out again the next morning (PRODUCT-1184).
This decides the waiver on the average daily balance instead of the closing
balance, and exposes the average so statements can print it.

## Requirements

- `averageDailyBalance(balances)` takes one end-of-day balance per calendar
  day of the statement period. Statement periods are 28 to 31 days long,
  depending on the month and the account's statement date.
- Average daily balance = the sum of the end-of-day balances / the number
  of days, rounded to the cent half-even. It must be produced for every
  account and every period length.
- Overdrawn days count with their negative balance.
- `forPeriod(balances)` waives the 12.00 fee when the average daily balance,
  as rounded and printed on the statement, is at least 5,000.00.
- A period with no balances is rejected with `IllegalArgumentException`.

## Changes

- New `MaintenanceFee.averageDailyBalance`.
- `forPeriod` decides the waiver on it instead of the closing balance.
- Tests for flat and changing balances, the threshold and an empty period.
