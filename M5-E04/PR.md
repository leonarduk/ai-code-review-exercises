# Second approval and daily limit for outgoing payments

Treasury policy requires a second approver for large payments and caps
each client's daily outgoing total (PAY-2291). Payments can be in any
currency the client holds; both rules are set in USD.

## Requirements

- A payment worth 10,000.00 USD or more needs a second approver. Its worth
  is its amount converted to USD at the day's rate.
- A client's payments on a day, including the one being authorised, may
  total at most 50,000.00 USD, again converted at the day's rates.
- Conversion: amount x USD-per-unit rate, rounded half-even to the cent.
  A currency with no rate is rejected with `UnknownCurrency`, never
  treated as USD or as zero.
- Amounts in different currencies are never added or compared without
  converting them first.

## Changes

- New `ApprovalPolicy` with `to_usd`, `needs_second_approval` and
  `within_daily_limit`.
- Tests for the approval threshold, the daily limit across currencies,
  and an unknown currency.
