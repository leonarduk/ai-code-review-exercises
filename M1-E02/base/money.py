"""Monetary rounding helpers."""

from decimal import Decimal, ROUND_HALF_EVEN

CENT = Decimal("0.01")


def round_cents(amount: Decimal) -> Decimal:
    """Round ``amount`` to the cent with banker's rounding."""
    return amount.quantize(CENT, rounding=ROUND_HALF_EVEN)
