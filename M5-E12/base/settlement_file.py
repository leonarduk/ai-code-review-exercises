"""Reads the acquirer's daily GBP settlement file."""

from collections.abc import Iterable, Iterator
from dataclasses import dataclass
from decimal import Decimal


@dataclass(frozen=True)
class Row:
    merchant: str
    payment_id: str
    amount: Decimal


def parse(lines: Iterable[str]) -> Iterator[Row]:
    """The file's rows, read lazily. The first line is a header and is skipped."""
    it = iter(lines)
    next(it, None)
    for line in it:
        if not line.strip():
            continue
        merchant, payment_id, amount = line.strip().split(",")
        yield Row(merchant, payment_id, Decimal(amount))
