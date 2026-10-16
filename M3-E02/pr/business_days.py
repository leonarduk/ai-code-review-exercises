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


def preceding(day: date, holidays: AbstractSet[date]) -> date:
    """Roll back to the last business day on or before day."""
    while not is_business_day(day, holidays):
        day -= ONE_DAY
    return day


def modified_following(day: date, holidays: AbstractSet[date]) -> date:
    """Roll forward to a business day, unless that crosses into the next
    month, in which case roll back instead (ISDA Modified Following)."""
    adjusted = following(day, holidays)
    if adjusted.month > day.month:
        return preceding(day, holidays)
    return adjusted


CONVENTIONS = {
    "following": following,
    "preceding": preceding,
    "modified_following": modified_following,
}


def adjust_schedule(
    dates: list[date], holidays: AbstractSet[date], convention: str = "modified_following"
) -> list[date]:
    """Adjust every unadjusted payment date in a schedule."""
    try:
        roll = CONVENTIONS[convention]
    except KeyError:
        raise ValueError(f"unknown business-day convention: {convention!r}") from None
    return [roll(day, holidays) for day in dates]
