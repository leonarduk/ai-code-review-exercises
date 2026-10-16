"""Trade dates and late flags for the Tokyo desk's trade confirmations."""

from __future__ import annotations

from datetime import date, datetime, timedelta, timezone
from zoneinfo import ZoneInfo

TOKYO = ZoneInfo("Asia/Tokyo")
CONFIRMATION_WINDOW = timedelta(minutes=15)


def from_epoch_millis(ms: int) -> datetime:
    """A venue API timestamp, in milliseconds since the Unix epoch, as aware UTC."""
    return datetime.fromtimestamp(ms / 1000, tz=timezone.utc)


def trade_date(value: date | datetime | int) -> date:
    """The desk's trade date for a booking date, a FIX timestamp or a venue timestamp."""
    if isinstance(value, date):
        return value
    if isinstance(value, datetime):
        if value.tzinfo is None:
            raise ValueError("FIX timestamps must be timezone-aware")
        return value.astimezone(TOKYO).date()
    if isinstance(value, int):
        return from_epoch_millis(value).astimezone(TOKYO).date()
    raise TypeError(f"no trade date for a {type(value).__name__}")


def is_late(executed_ms: int, received_ms: int) -> bool:
    """Whether a confirmation arrived more than the window after the execution."""
    return received_ms - executed_ms > CONFIRMATION_WINDOW.total_seconds()
