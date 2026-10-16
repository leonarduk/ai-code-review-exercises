# Send online-banking transfers as pain.001 messages

The bank is retiring the CSV upload for corporate transfers at the end of
the quarter (PAY-1477). This writes each customer's batch of euro transfers
as an ISO 20022 pain.001 message instead.

## Requirements

- The group header carries the message id, the number of transfers and
  their control sum.
- Transfers are grouped into one PmtInf block per requested execution date.
  The blocks appear in order of execution date, earliest first: the bank's
  file check rejects a message whose blocks are out of date order. Within a
  block, transfers keep the order the customer entered them.
- Each transfer carries its end-to-end id, amount, creditor name, creditor
  IBAN and the remittance information the customer typed.
- Amounts are written with exactly two decimals and never in exponent form.
  An amount with more than two decimal places is rejected.
- Free text the customer entered, the creditor name and the remittance
  information, is written exactly as entered, escaped as XML requires. The
  bank's parser rejects a message that is not well-formed XML, and the
  whole batch with it.
- A message has at least one transfer.

## Changes

- New `Pain001Writer` with `write`.
- Tests for the group header, grouping by date, escaping a name, and the
  rejection of an empty message and of three decimals.
