"""Money-market dates: business-day adjustment and tenor maturities."""

from __future__ import annotations

from datetime import date, timedelta
from typing import AbstractSet


def is_business_day(day: date, holidays: AbstractSet[date]) -> bool:
    return day.weekday() < 5 and day not in holidays


def modified_following(day: date, holidays: AbstractSet[date]) -> date:
    """Roll forward to a business day, or back if rolling forward leaves the month."""
    forward = day
    while not is_business_day(forward, holidays):
        forward += timedelta(days=1)
    if forward.month == day.month:
        return forward
    back = day
    while not is_business_day(back, holidays):
        back -= timedelta(days=1)
    return back
