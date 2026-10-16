"""General-ledger journal entries."""

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


class GeneralLedger:
    def __init__(self):
        self.entries: list[JournalEntry] = []

    def post(self, entry: JournalEntry) -> None:
        self.entries.append(entry)
