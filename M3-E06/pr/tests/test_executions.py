from datetime import datetime, timezone

import pytest

from executions import TimestampError, execution, merge, parse_iso, parse_legacy

UTC = timezone.utc


def test_parse_iso_with_z_and_offsets():
    assert parse_iso("2026-03-10T14:15:00Z") == datetime(2026, 3, 10, 14, 15, tzinfo=UTC)
    assert parse_iso("2026-03-10T09:30:00-05:00") == datetime(2026, 3, 10, 14, 30, tzinfo=UTC)


def test_parse_iso_rejects_a_missing_offset():
    with pytest.raises(TimestampError):
        parse_iso("2026-03-10T14:15:00")


def test_parse_legacy_in_winter_and_summer():
    assert parse_legacy("2026-01-15 09:00:00") == datetime(2026, 1, 15, 9, 0, tzinfo=UTC)
    assert parse_legacy("2026-07-15 09:00:00") == datetime(2026, 7, 15, 8, 0, tzinfo=UTC)


def test_merge_orders_executions():
    reports = [
        execution("LSE", "L-2", "2026-03-10T14:15:00Z"),
        execution("LSE", "L-1", "2026-03-10T08:05:00Z"),
        execution("LSE", "L-3", "2026-03-10T16:29:59Z"),
    ]
    assert [e.execution_id for e in merge(reports)] == ["L-1", "L-2", "L-3"]
