# Age settlement fails in London business days

Fails are aged in calendar days, so a fail over a bank-holiday weekend jumps
two buckets without a single settlement window having passed (OPS-2318).
This moves the ageing to London business days and to the new thresholds
agreed with the operations team.

## Requirements

- A fail's age is the number of London business days after its intended
  settlement date, up to and including today. The intended settlement date
  itself is not counted; a fail due today, or in the future, is aged 0.
- A London business day is a Monday to Friday that is not in the London
  holiday file. The file has one ISO date per line; blank lines and lines
  starting with `#` are ignored.
- Today is the current date in London, whatever the time zone of the
  server running the report.
- Buckets: age 0 is CURRENT, 1 to 4 is MONITOR, 5 to 14 is ESCALATE, and
  15 or more is BUY_IN.

## Changes

- `FailAgeing` takes the holiday file's lines, and `daysOutstanding`
  becomes `businessDaysOutstanding`.
- New thresholds: 5 and 15 business days (were 7 and 21 calendar days).
- Tests for a fail due today, a weekend, the Easter holidays and a buy-in.
