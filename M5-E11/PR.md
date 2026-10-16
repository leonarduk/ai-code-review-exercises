# Mask card numbers and IBANs in payment logs

A log review found full card numbers and IBANs in the application log,
which is shipped to the log platform and kept for a year (SEC-311). PCI
DSS does not allow a full card number to be stored there. This masks
them before anything is logged.

## Requirements

- A card number (PAN) is logged with only its last four digits visible;
  every other digit is replaced by `*`.
- An IBAN is logged with only its country code and its last four
  characters visible.
- Card numbers are 13 to 19 digits long, depending on the scheme.
  Customers type them as one run of digits or in groups separated by
  single spaces or hyphens.
- The payment reference is free text that customers fill in, and
  some type their card number into it. Any card number in the reference
  is masked in the same way before the reference is logged.
- The payer is a card number when the method is `card` and an IBAN
  otherwise.

## Changes

- New `mask_pan`, `mask_iban` and `redact`; `log_line` uses them.
- Tests for each, and for a full log line.
