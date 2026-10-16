"""Applies payment instructions from the payments.instructions topic (PAY-2210)."""

from dataclasses import dataclass
from decimal import Decimal

from ledger import InsufficientFunds, Ledger


@dataclass(frozen=True)
class Message:
    partition: int
    offset: int
    instruction_id: str
    debit_account: str
    credit_account: str
    amount: Decimal

    @classmethod
    def from_record(cls, partition: int, offset: int, record: dict) -> "Message":
        return cls(
            partition=partition,
            offset=offset,
            instruction_id=record["instruction_id"],
            debit_account=record["debit"],
            credit_account=record["credit"],
            amount=Decimal(record["amount"]),
        )


class PaymentConsumer:
    def __init__(self, ledger: Ledger):
        self._ledger = ledger
        self._processed: set = set()
        self.rejected: list[str] = []

    def handle(self, message: Message) -> bool:
        """Apply one delivery. True if this call posted it, False if it was a duplicate."""
        key = (message.partition, message.offset)
        if key in self._processed:
            return False
        self._processed.add(key)
        try:
            self._ledger.transfer(message.debit_account, message.credit_account, message.amount)
        except InsufficientFunds:
            self.rejected.append(message.instruction_id)
            raise
        return True
