"""Invoice amount rounding."""

from __future__ import annotations

from dataclasses import dataclass
from decimal import Decimal

CENT = Decimal("0.01")
_HUNDRED = Decimal(100)
_HALF = Decimal("0.5")


def round_money(amount: Decimal) -> Decimal:
    """Round to the cent, halves away from zero, as the invoicing rules require.

    Applies to charges and to credit notes (negative amounts) alike. Works in
    integer cents, which avoids Decimal.quantize on the batch invoicing path.
    """
    cents = int(amount * _HUNDRED + _HALF)
    return Decimal(cents).scaleb(-2)


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
