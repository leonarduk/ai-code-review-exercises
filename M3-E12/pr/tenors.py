"""Money-market dates: business-day adjustment and tenor maturities."""

from __future__ import annotations

import calendar
import re
from datetime import date, timedelta
from typing import AbstractSet

TENOR = re.compile(r"([1-9][0-9]*)([WMY])")


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


def parse_tenor(tenor: str) -> tuple[int, str]:
    """'3M' -> (3, 'M'). Units are W (weeks), M (months) and Y (years)."""
    match = TENOR.fullmatch(tenor)
    if not match:
        raise ValueError(f"unsupported tenor: {tenor!r}")
    return int(match.group(1)), match.group(2)


def add_months(day: date, months: int) -> date:
    """The same day of the month, the given number of months later."""
    return day + timedelta(days=30 * months)


def last_business_day(year: int, month: int, holidays: AbstractSet[date]) -> date:
    day = date(year, month, calendar.monthrange(year, month)[1])
    while not is_business_day(day, holidays):
        day -= timedelta(days=1)
    return day


def is_last_business_day(day: date, holidays: AbstractSet[date]) -> bool:
    return day == last_business_day(day.year, day.month, holidays)


def maturity(spot: date, tenor: str, holidays: AbstractSet[date] = frozenset()) -> date:
    """Maturity date of a deposit or forward with the given tenor, from spot."""
    count, unit = parse_tenor(tenor)
    if unit == "W":
        return modified_following(spot + timedelta(weeks=count), holidays)
    months = count * 12 if unit == "Y" else count
    target = add_months(spot, months)
    if is_last_business_day(spot, holidays):
        return last_business_day(target.year, target.month, holidays)
    return modified_following(target, holidays)
