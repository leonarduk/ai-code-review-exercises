# Group header for outgoing pain.001 batches

The bank's SEPA gateway now validates the group header of every flat-file
batch we send (PAY-1184). Until now the batch job wrote transaction lines
only and the gateway filled in the header itself. This adds the header.

## Requirements

- The header line is `MsgId|CreDtTm|NbOfTxs|CtrlSum|Ccy`.
- MsgId is `MSG-`, the creation time as `yyyyMMddHHmmss`, a hyphen and the
  batch's sequence number of the day as four digits (`0001` to `9999`). A
  sequence outside 1 to 9999 is rejected.
- CreDtTm is the creation time in UTC, to the second, as
  `yyyy-MM-ddTHH:mm:ss`. The time comes from the injected `Clock`; the
  batch hosts run in London and Frankfurt, and the gateway wants UTC from
  both.
- NbOfTxs is the number of transfers. CtrlSum is the sum of their amounts
  with a full stop and exactly two decimals (`1234.50`), with no thousands
  separator, on every host whatever its default locale.
- A batch has at least one transfer, all in the batch currency, each with a
  positive amount of at most two decimal places. Channels send amounts with
  trailing zeros (`10.500`); those are still two decimal places.
- A header describes the transfers it was built from: adding to or removing
  from the caller's list afterwards does not change its count or sum.

## Changes

- New `GroupHeader` record with `of`, `numberOfTransactions`, `controlSum`
  and `line`.
- Tests for a two-transfer header, the control sum, and rejection of a
  mixed-currency batch, an empty batch and a five-digit sequence.
