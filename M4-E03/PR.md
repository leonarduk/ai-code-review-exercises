# FX option expiry instant and time to expiry

The pricer needs the exact expiry instant of each option and its time to
expiry (OPT-772). Options expire at a cut: a local time of day in a named
market (10:00 New York, 15:00 Tokyo, 14:00 London) on the expiry date.

## Requirements

- The expiry instant is the cut's local time on the expiry date, in the
  cut's market, as observed that day (summer or winter time).
- An option is expired from the expiry instant onwards; at exactly the cut
  it is expired.
- Time to expiry is the real elapsed time from now to the expiry instant,
  in seconds, divided by the seconds in a 365-day year. Once expired, it is
  zero.
- "Now" comes from an injected `Clock`, so that the pricer and its tests
  control it.

## Changes

- New `OptionExpiry` with `instant`, `isExpired` and `yearsToExpiry`.
- Tests for a New York and a Tokyo cut, the expiry boundary and time to
  expiry.
