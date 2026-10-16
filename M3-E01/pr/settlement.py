"""Settlement dates for cash equity trades."""

from __future__ import annotations

import json
from datetime import date, timedelta
from pathlib import Path
from typing import Iterable

CALENDAR_PATH = Path(__file__).with_name("market_calendar.json")


class MarketCalendar:
    """Business days for one market: weekdays that are not market holidays."""

    def __init__(self, holidays: Iterable[date]):
        self._holidays = set(holidays)

    @classmethod
    def load(cls, path: Path = CALENDAR_PATH) -> MarketCalendar:
        with path.open(encoding="utf-8") as handle:
            data = json.load(handle)
        return cls(entry["date"] for entry in data["holidays"])

    @property
    def holiday_count(self) -> int:
        return len(self._holidays)

    def is_business_day(self, day: date) -> bool:
        return day.weekday() < 5 and day not in self._holidays

    def next_business_day(self, day: date) -> date:
        candidate = day + timedelta(days=1)
        while not self.is_business_day(candidate):
            candidate += timedelta(days=1)
        return candidate


def settlement_date(
    trade_date: date, cycle_days: int, calendar: MarketCalendar | None = None
) -> date:
    """Settlement date for a trade on trade_date with a T+cycle_days cycle."""
    if cycle_days < 0:
        raise ValueError(f"settlement cycle cannot be negative: T+{cycle_days}")
    calendar = calendar or MarketCalendar.load()
    candidate = trade_date + timedelta(days=cycle_days)
    if not calendar.is_business_day(candidate):
        candidate = calendar.next_business_day(candidate)
    return candidate
