# Fixed amount format in the bank file, and read the return file

The bank rejected two of last week's payment files (PAY-1302): `toString()`
wrote `250.5` for one amount and `1E+3` for another. This writes every
amount in the bank's format, and adds a reader for the return file the bank
sends back with the outcome of each payment.

## Requirements

Payment file (`reference,iban,amount,currency`):

- The amount is written with a full stop as the decimal separator, exactly
  two decimals and no thousands separator: `250.50`, `1000.00`, on
  whichever batch host generates the file.
- Only EUR, GBP and USD are accepted. Amounts must be positive with at most
  two decimal places (a scale of 2 or less); a reference cannot contain a
  comma.

Return file (`reference,status,amount,reasonCode,reasonText`, after a
header line):

- Status `ACCP` is accepted and `RJCT` rejected; any other status is an
  error.
- Every line has five fields. The reason code and reason text are optional:
  the bank leaves either or both empty (accepted payments usually have
  neither), and an empty field is read as an empty string.
- A malformed line fails the whole file, so it can be requested again.

## Changes

- `PaymentFileWriter.line` validates and formats the amount.
- New `PaymentReturn` record and `ReturnFileReader` with `parse` and
  `read`.
- Tests for two-decimal amounts, the header, the currency and scale checks,
  accepted and rejected return lines, and malformed lines.
