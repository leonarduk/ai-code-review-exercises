"""Intraday liquidity charges for overdrawn settlement accounts."""

from datetime import date
from decimal import Decimal, ROUND_HALF_EVEN

CENT = Decimal("0.01")
DAYS_PER_YEAR = Decimal(365)


def daily_charge(average_overdraft: Decimal, annual_rate: Decimal, day: date) -> Decimal:
    """One day's charge on the average overdrawn balance."""
    charge = average_overdraft * annual_rate / DAYS_PER_YEAR
    return charge.quantize(CENT, rounding=ROUND_HALF_EVEN)
