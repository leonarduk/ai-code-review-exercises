# Trailer record for clearing submissions

The clearing house now requires a trailer at the end of every submission so
it can check it received the whole file (CLR-219). This adds the trailer,
and a way to build payments from amounts in pounds.

## Requirements

- The trailer is `TRL`, the number of payments as 7 digits and the total of
  their amounts in pence as 14 digits, both zero-padded:
  `TRL000000200000000020000` for two payments totalling 200.00.
- The total is exact. Treasury sweeps and supplier runs go through the same
  submissions, and a single submission can be well over 100 million pounds;
  a total that does not fit in 14 digits is rejected rather than written.
- A submission has between 1 and 9,999,999 payments, each with a positive
  amount.
- `Payment.ofPounds` converts an amount in pounds to pence; an amount with a
  fraction of a penny is rejected.
- The trailer comes after the last payment record.

## Changes

- New `SubmissionTrailer` record with `of`, `line` and `file`.
- `Payment.ofPounds`.
- Tests for the count and total, the trailer line, the file, the
  conversion to pence and the rejection of fractional, empty and zero
  amounts.
