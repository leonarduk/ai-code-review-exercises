"""Business-day adjustment of scheduled payment dates."""

from datetime import date, timedelta
from typing import AbstractSet

ONE_DAY = timedelta(days=1)


def is_business_day(day: date, holidays: AbstractSet[date]) -> bool:
    return day.weekday() < 5 and day not in holidays


def following(day: date, holidays: AbstractSet[date]) -> date:
    """Roll forward to the first business day on or after day."""
    while not is_business_day(day, holidays):
        day += ONE_DAY
    return day
