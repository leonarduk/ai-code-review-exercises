"""CSDR cash penalties for late-settling instructions."""

from __future__ import annotations

from datetime import date, timedelta
from decimal import Decimal, ROUND_HALF_EVEN
from typing import AbstractSet

CENT = Decimal("0.01")
BASIS_POINTS = Decimal(10000)


def business_days_late(intended: date, actual: date, holidays: AbstractSet[date]) -> int:
    """Business days in [intended, actual): each day the instruction failed."""
    if actual <= intended:
        return 0
    days = 0
    day = intended
    while day < actual:
        if day.weekday() < 5 and day not in holidays:
            days += 1
        day += timedelta(days=1)
    return days


def penalty(
    instruction_value: Decimal,
    daily_rate_bps: Decimal,
    intended: date,
    actual: date,
    holidays: AbstractSet[date],
) -> Decimal:
    """Penalty = value x daily rate x business days late, rounded to the cent."""
    days = business_days_late(intended, actual, holidays)
    amount = instruction_value * daily_rate_bps / BASIS_POINTS * days
    return amount.quantize(CENT, rounding=ROUND_HALF_EVEN)
