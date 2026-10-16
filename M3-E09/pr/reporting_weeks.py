"""Weekly buckets for the transaction report, by ISO 8601 week."""

from __future__ import annotations

import re
from datetime import date, datetime, timedelta
from zoneinfo import ZoneInfo

LONDON = ZoneInfo("Europe/London")
LABEL = re.compile(r"(\d{4})-W(\d{2})")


def week_label(day: date) -> str:
    """ISO 8601 week of a trade date, e.g. 2026-W07."""
    return day.strftime("%G-W%V")


def weeks_in_year(year: int) -> int:
    """52 or 53: 28 December always falls in the last ISO week of its year."""
    return date(year, 12, 28).isocalendar().week


def week_bounds(label: str) -> tuple[date, date]:
    """Monday and Sunday of the week a label names."""
    match = LABEL.fullmatch(label)
    if not match:
        raise ValueError(f"not an ISO week label: {label!r}")
    year, week = int(match.group(1)), int(match.group(2))
    if not 1 <= week <= weeks_in_year(year):
        raise ValueError(f"{year} has no week {week}")
    monday = date.fromisocalendar(year, week, 1)
    return monday, monday + timedelta(days=6)


def report_week(executed_at: datetime) -> str:
    """Reporting week of an execution: the ISO week of its London trade date."""
    if executed_at.tzinfo is None:
        raise ValueError("execution timestamps must be timezone-aware")
    return week_label(executed_at.astimezone(LONDON).date())
