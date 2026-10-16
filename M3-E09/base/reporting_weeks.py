"""Weekly buckets for the transaction report."""

from datetime import date


def week_label(day: date) -> str:
    """Reporting week of a trade date (weeks start on Sunday)."""
    return day.strftime("%Y-%U")
