"""Interest accrual for term deposits."""

from decimal import Decimal, ROUND_HALF_EVEN

CENT = Decimal("0.01")


def simple_interest(principal: Decimal, annual_rate: Decimal, days: int) -> Decimal:
    """Simple interest for a number of days, ACT/365 Fixed, rounded to the cent."""
    interest = principal * annual_rate * days / Decimal(365)
    return interest.quantize(CENT, rounding=ROUND_HALF_EVEN)
