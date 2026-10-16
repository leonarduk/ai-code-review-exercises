from datetime import date, datetime, timezone

import pytest

from snapshots import latest_snapshot, snapshot_instant

UTC = timezone.utc


def test_winter_snapshot_is_22_00_utc():
    assert snapshot_instant(date(2026, 1, 15)) == datetime(2026, 1, 15, 22, 0, tzinfo=UTC)


def test_latest_snapshot_after_the_close():
    assert latest_snapshot(datetime(2026, 1, 15, 23, 0, tzinfo=UTC)) == datetime(2026, 1, 15, 22, 0, tzinfo=UTC)


def test_latest_snapshot_before_the_close_is_the_previous_day():
    assert latest_snapshot(datetime(2026, 1, 15, 12, 0, tzinfo=UTC)) == datetime(2026, 1, 14, 22, 0, tzinfo=UTC)


def test_weekend_dates_have_no_snapshot():
    with pytest.raises(ValueError):
        snapshot_instant(date(2026, 1, 17))
