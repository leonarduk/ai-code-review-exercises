"""Merge execution reports from several venues into one timeline."""

from __future__ import annotations

from dataclasses import dataclass
from datetime import datetime, timezone
from zoneinfo import ZoneInfo

LEGACY_VENUE_ZONE = ZoneInfo("Europe/London")
LEGACY_FORMAT = "%Y-%m-%d %H:%M:%S"


class TimestampError(ValueError):
    """A venue timestamp that cannot be placed on the timeline."""


@dataclass(frozen=True)
class Execution:
    venue: str
    execution_id: str
    timestamp: str          # as reported by the venue
    executed_at: datetime   # timezone-aware, UTC


def parse_iso(text: str) -> datetime:
    """ISO 8601 venues: the timestamp must carry an offset or Z."""
    try:
        parsed = datetime.fromisoformat(text)
    except ValueError:
        raise TimestampError(f"not an ISO 8601 timestamp: {text!r}") from None
    if parsed.tzinfo is None:
        raise TimestampError(f"timestamp has no UTC offset: {text!r}")
    return parsed.astimezone(timezone.utc)


def parse_legacy(text: str) -> datetime:
    """The legacy venue reports London wall-clock time without an offset."""
    local = datetime.strptime(text, LEGACY_FORMAT).replace(tzinfo=LEGACY_VENUE_ZONE)
    return local.astimezone(timezone.utc)


def execution(venue: str, execution_id: str, timestamp: str, legacy: bool = False) -> Execution:
    executed_at = parse_legacy(timestamp) if legacy else parse_iso(timestamp)
    return Execution(venue, execution_id, timestamp, executed_at)


def merge(executions: list[Execution]) -> list[Execution]:
    """Every execution in time order, earliest first; ties keep input order."""
    return sorted(executions, key=lambda e: e.timestamp)
