"""General-ledger journal entries, validated before posting (GL-718)."""

from dataclasses import dataclass
from decimal import Decimal
from enum import Enum


class Side(Enum):
    DEBIT = "debit"
    CREDIT = "credit"


@dataclass(frozen=True)
class Line:
    account: str
    side: Side
    amount: Decimal
    currency: str


@dataclass(frozen=True)
class JournalEntry:
    entry_id: str
    lines: tuple[Line, ...]


class UnbalancedEntry(Exception):
    """Debits and credits of a journal entry do not agree."""


def validate(entry: JournalEntry) -> None:
    if len(entry.lines) < 2:
        raise ValueError(f"{entry.entry_id}: an entry needs at least two lines")
    for line in entry.lines:
        if line.amount <= 0:
            raise ValueError(f"{entry.entry_id}: amounts are positive; the side gives the sign")
    debits = sum(line.amount for line in entry.lines if line.side is Side.DEBIT)
    credits = sum(line.amount for line in entry.lines if line.side is Side.CREDIT)
    if debits != credits:
        raise UnbalancedEntry(f"{entry.entry_id}: debits {debits} != credits {credits}")


class GeneralLedger:
    def __init__(self):
        self.entries: list[JournalEntry] = []

    def post(self, entry: JournalEntry) -> None:
        validate(entry)
        self.entries.append(entry)
