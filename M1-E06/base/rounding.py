"""Invoice amount rounding."""

from __future__ import annotations

from dataclasses import dataclass
from decimal import Decimal, ROUND_HALF_UP

CENT = Decimal("0.01")


def round_money(amount: Decimal) -> Decimal:
    """Round to the cent, halves away from zero, as the invoicing rules require.

    Applies to charges and to credit notes (negative amounts) alike.
    """
    return amount.quantize(CENT, rounding=ROUND_HALF_UP)


@dataclass(frozen=True)
class InvoiceLine:
    description: str
    net: Decimal
    tax_rate: Decimal

    @property
    def tax(self) -> Decimal:
        return round_money(self.net * self.tax_rate)

    @property
    def gross(self) -> Decimal:
        return round_money(self.net) + self.tax
