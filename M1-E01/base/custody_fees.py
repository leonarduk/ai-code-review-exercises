"""Annual custody fee calculation."""

from decimal import Decimal, ROUND_HALF_EVEN

CENT = Decimal("0.01")
FLAT_RATE = Decimal("0.0025")
MINIMUM_FEE = Decimal("25.00")


def annual_fee(holding_value: Decimal) -> Decimal:
    """Annual custody fee: a flat 0.25% of holding value, minimum 25.00."""
    if holding_value < 0:
        raise ValueError("holding value cannot be negative")
    fee = (holding_value * FLAT_RATE).quantize(CENT, rounding=ROUND_HALF_EVEN)
    return max(fee, MINIMUM_FEE)
