"""Merchant service charges on card sales."""

from decimal import Decimal, ROUND_DOWN

CENT = Decimal("0.01")
RATE = Decimal("0.0195")


def sale_fee(amount: Decimal) -> Decimal:
    """Fee on a card sale: 1.95% of the amount, truncated toward zero to the cent."""
    if amount <= 0:
        raise ValueError("sale amount must be positive")
    return (amount * RATE).quantize(CENT, rounding=ROUND_DOWN)
