"""Currency conversion helpers."""

from decimal import Decimal

CENT = Decimal("0.01")


def to_usd(amount: Decimal, usd_per_unit: Decimal) -> Decimal:
    """Convert ``amount`` to USD, given the USD price of one unit."""
    return (amount * usd_per_unit).quantize(CENT)
