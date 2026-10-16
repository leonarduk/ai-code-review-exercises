"""Intraday liquidity charges for overdrawn settlement accounts.

Charged per hour of the London calendar day, so the clock-change days
(23 and 25 hours long) are charged for the hours they actually have.
"""

from __future__ import annotations

from datetime import date, datetime, time, timedelta
from decimal import Decimal, ROUND_HALF_EVEN
from zoneinfo import ZoneInfo

LONDON = ZoneInfo("Europe/London")
CENT = Decimal("0.01")
HOURS_PER_YEAR = Decimal(365 * 24)
SECONDS_PER_HOUR = Decimal(3600)


def london_day_bounds(day: date) -> tuple[datetime, datetime]:
    """Local midnight at the start of day and at the start of the next day."""
    start = datetime.combine(day, time.min, tzinfo=LONDON)
    end = datetime.combine(day + timedelta(days=1), time.min, tzinfo=LONDON)
    return start, end


def hours_in_day(day: date) -> Decimal:
    """Length of the London calendar day, in hours."""
    start, end = london_day_bounds(day)
    return Decimal((end - start).total_seconds()) / SECONDS_PER_HOUR


def daily_charge(average_overdraft: Decimal, annual_rate: Decimal, day: date) -> Decimal:
    """One London day's charge on the average overdrawn balance."""
    if average_overdraft < 0:
        raise ValueError("average overdraft must not be negative")
    charge = average_overdraft * annual_rate * hours_in_day(day) / HOURS_PER_YEAR
    return charge.quantize(CENT, rounding=ROUND_HALF_EVEN)
