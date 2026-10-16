"""Matches bank statement lines against expected ledger receipts in GBP (OPS-2402)."""

from collections.abc import Iterable
from dataclasses import dataclass, field
from datetime import date
from decimal import ROUND_HALF_EVEN, Decimal

PENNY = Decimal("0.01")


@dataclass
class Report:
    matched: list[str] = field(default_factory=list)
    unmatched_statement: list[str] = field(default_factory=list)
    unmatched_ledger: list[str] = field(default_factory=list)
    malformed: list[tuple[int, str]] = field(default_factory=list)


def parse_line(line: str, gbp_per_unit: dict[str, Decimal]) -> tuple[str, Decimal]:
    """(reference, amount in GBP) from one statement line."""
    day, reference, amount, currency = line.split(",")
    date.fromisoformat(day)
    rate = gbp_per_unit.get(currency.strip(), Decimal("1"))
    value = Decimal(amount)
    if not value.is_finite():
        raise ValueError(f"not an amount: {amount!r}")
    return reference.strip(), (value * rate).quantize(PENNY, rounding=ROUND_HALF_EVEN)


def reconcile(lines: Iterable[str], ledger: dict[str, Decimal], gbp_per_unit: dict[str, Decimal]) -> Report:
    report = Report()
    outstanding = dict(ledger)
    for number, line in enumerate(lines, start=1):
        if not line.strip():
            continue
        try:
            reference, gbp = parse_line(line, gbp_per_unit)
        except Exception:
            continue
        expected = outstanding.pop(reference, None)
        if expected == gbp:
            report.matched.append(reference)
        else:
            report.unmatched_statement.append(reference)
            if expected is not None:
                outstanding[reference] = expected
    report.unmatched_ledger = sorted(outstanding)
    return report
