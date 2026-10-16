"""End-of-day position snapshots, taken at 17:00 New York time."""

from __future__ import annotations

from datetime import date, datetime, time, timedelta, timezone
from typing import AbstractSet
from zoneinfo import ZoneInfo

NEW_YORK = ZoneInfo("America/New_York")
SNAPSHOT_TIME = time(17, 0)


def _is_business_day(day: date, holidays: AbstractSet[date]) -> bool:
    return day.weekday() < 5 and day not in holidays


def snapshot_instant(business_date: date, holidays: AbstractSet[date] = frozenset()) -> datetime:
    """The UTC instant of the end-of-day snapshot for business_date."""
    if not _is_business_day(business_date, holidays):
        raise ValueError(f"{business_date} is not a business day")
    local = datetime.combine(business_date, SNAPSHOT_TIME, tzinfo=NEW_YORK)
    return local.astimezone(timezone.utc)


def latest_snapshot(moment: datetime, holidays: AbstractSet[date] = frozenset()) -> datetime:
    """The most recent snapshot instant at or before moment."""
    if moment.tzinfo is None:
        raise ValueError("moment must be timezone-aware")
    day = moment.astimezone(NEW_YORK).date()
    while True:
        if _is_business_day(day, holidays):
            candidate = snapshot_instant(day, holidays)
            if candidate <= moment:
                return candidate
        day -= timedelta(days=1)
