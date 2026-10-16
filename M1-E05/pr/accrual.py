"""Interest accrual for term deposits."""

from __future__ import annotations

from dataclasses import dataclass
from datetime import date, timedelta
from decimal import Decimal, ROUND_HALF_EVEN
from typing import Iterator

CENT = Decimal("0.01")
DAYS_IN_YEAR = Decimal(365)  # ACT/365 Fixed


@dataclass(frozen=True)
class DailyAccrual:
    """Interest accrued for a single calendar day, before any rounding."""

    accrual_date: date
    amount: Decimal


def simple_interest(principal: Decimal, annual_rate: Decimal, days: int) -> Decimal:
    """Simple interest for a number of days, ACT/365 Fixed, rounded to the cent."""
    interest = principal * annual_rate * days / DAYS_IN_YEAR
    return interest.quantize(CENT, rounding=ROUND_HALF_EVEN)


def daily_accruals(
    principal: Decimal, annual_rate: Decimal, start: date, end: date
) -> Iterator[DailyAccrual]:
    """Yield one unrounded accrual per day of the accrual period."""
    if end < start:
        raise ValueError(f"accrual end {end} is before start {start}")
    daily_amount = principal * annual_rate / DAYS_IN_YEAR
    for offset in range((end - start).days + 1):
        yield DailyAccrual(start + timedelta(days=offset), daily_amount)


def accrued_interest(
    principal: Decimal, annual_rate: Decimal, start: date, end: date
) -> Decimal:
    """Interest accrued over the period, rounded once to the cent."""
    total = sum(
        (accrual.amount for accrual in daily_accruals(principal, annual_rate, start, end)),
        Decimal(0),
    )
    return total.quantize(CENT, rounding=ROUND_HALF_EVEN)
