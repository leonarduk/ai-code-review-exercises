"""Account-opening anniversaries for the annual custody fee."""

from __future__ import annotations

from datetime import date


def anniversary(opened: date, year: int) -> date:
    """The opening date's month and day in the given year."""
    try:
        return opened.replace(year=year)
    except ValueError:
        # 29 February in a year that has no 29 February
        return date(year, 3, 1)


def completed_years(opened: date, on: date) -> int:
    """Whole years the account has been open on the given date."""
    if on < opened:
        raise ValueError(f"{on} is before the account was opened on {opened}")
    return (on - opened).days // 365


def next_anniversary(opened: date, after: date) -> date:
    """The first anniversary strictly after the given date."""
    candidate = anniversary(opened, max(after.year, opened.year + 1))
    if candidate <= after:
        candidate = anniversary(opened, candidate.year + 1)
    return candidate
