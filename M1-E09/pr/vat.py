"""VAT rates, VAT-exclusive pricing and VAT analysis of till receipts."""

from __future__ import annotations

from dataclasses import dataclass
from decimal import Decimal, ROUND_HALF_EVEN
from typing import Iterable

CENT = Decimal("0.01")

VAT_RATES = {
    "S": Decimal("0.20"),  # standard
    "R": Decimal("0.05"),  # reduced
    "Z": Decimal("0.00"),  # zero-rated
}
CODE_ORDER = tuple(VAT_RATES)


def vat_rate(code: str) -> Decimal:
    """The VAT rate for a VAT code; unknown codes raise ValueError."""
    try:
        return VAT_RATES[code]
    except KeyError:
        raise ValueError(f"unknown VAT code {code!r}") from None


def add_vat(net: Decimal, code: str) -> Decimal:
    """Gross price for a VAT-exclusive net price, VAT rounded half-even to the cent."""
    vat = (net * vat_rate(code)).quantize(CENT, rounding=ROUND_HALF_EVEN)
    return net + vat


def extract_vat(gross: Decimal, code: str) -> tuple[Decimal, Decimal]:
    """Split a VAT-inclusive amount into (net, vat), which sum to ``gross``."""
    rate = vat_rate(code)
    net = (gross / (1 + rate)).quantize(CENT, rounding=ROUND_HALF_EVEN)
    return net, gross - net


@dataclass(frozen=True)
class ReceiptLine:
    description: str
    unit_price: Decimal  # VAT-inclusive shelf price
    quantity: int  # negative for a refunded item
    vat_code: str

    @property
    def gross(self) -> Decimal:
        return self.unit_price * self.quantity


@dataclass(frozen=True)
class VatRow:
    vat_code: str
    rate: Decimal
    gross: Decimal
    net: Decimal
    vat: Decimal


def vat_summary(lines: Iterable[ReceiptLine]) -> list[VatRow]:
    """The receipt's VAT analysis: one row per VAT code used, in code order."""
    totals: dict[str, tuple[Decimal, Decimal, Decimal]] = {}
    for line in lines:
        net, vat = extract_vat(line.gross, line.vat_code)
        gross_sum, net_sum, vat_sum = totals.get(line.vat_code, (Decimal("0.00"),) * 3)
        totals[line.vat_code] = (gross_sum + line.gross, net_sum + net, vat_sum + vat)
    return [
        VatRow(code, vat_rate(code), *totals[code])
        for code in sorted(totals, key=CODE_ORDER.index)
    ]


def total_vat(lines: Iterable[ReceiptLine]) -> Decimal:
    """VAT on the whole receipt: the sum of the per-code VAT."""
    return sum((row.vat for row in vat_summary(lines)), Decimal("0.00"))
