"""Booking date for client orders received around the New York cut-off."""

from __future__ import annotations

from datetime import date, datetime, time, timedelta, timezone
from typing import AbstractSet

NEW_YORK = timezone(timedelta(hours=-5), "America/New_York")
CUT_OFF = time(17, 0)
FEED_FORMAT = "%Y-%m-%d %H:%M:%S"


def parse_feed_timestamp(text: str) -> datetime:
    """Parse an order-feed timestamp; the feed writes UTC without an offset."""
    return datetime.strptime(text.strip(), FEED_FORMAT).astimezone(timezone.utc)


def booking_date(received_at: datetime, holidays: AbstractSet[date] = frozenset()) -> date:
    """Book on the New York business day of receipt, or the next business
    day if the order arrives at or after 17:00 New York time."""
    if received_at.tzinfo is None:
        raise ValueError("received_at must be timezone-aware")
    local = received_at.astimezone(NEW_YORK)
    day = local.date()
    if local.time() >= CUT_OFF or not _is_business_day(day, holidays):
        day = _next_business_day(day, holidays)
    return day


def _is_business_day(day: date, holidays: AbstractSet[date]) -> bool:
    return day.weekday() < 5 and day not in holidays


def _next_business_day(day: date, holidays: AbstractSet[date]) -> date:
    day += timedelta(days=1)
    while not _is_business_day(day, holidays):
        day += timedelta(days=1)
    return day
