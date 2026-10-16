"""Savings account interest."""

from decimal import Decimal, ROUND_HALF_EVEN

CENT = Decimal("0.01")


def annual_interest(balance: Decimal, annual_rate: Decimal) -> Decimal:
    """Simple interest for a year on a constant balance, rounded half-even to the cent."""
    if balance < 0 or annual_rate < 0:
        raise ValueError("balance and rate must be non-negative")
    return (balance * annual_rate).quantize(CENT, rounding=ROUND_HALF_EVEN)
