from datetime import date, datetime, timezone

import pytest

from cutoff import booking_date, parse_feed_timestamp

UTC = timezone.utc


@pytest.mark.parametrize(
    ("received_at", "expected"),
    [
        (datetime(2026, 1, 15, 21, 30, tzinfo=UTC), date(2026, 1, 15)),  # 16:30 New York
        (datetime(2026, 1, 15, 22, 30, tzinfo=UTC), date(2026, 1, 16)),  # 17:30 New York
        (datetime(2026, 1, 16, 23, 0, tzinfo=UTC), date(2026, 1, 19)),   # Friday evening
    ],
)
def test_booking_date(received_at, expected):
    assert booking_date(received_at) == expected


def test_holidays_are_skipped():
    received = datetime(2026, 1, 16, 23, 0, tzinfo=UTC)
    assert booking_date(received, {date(2026, 1, 19)}) == date(2026, 1, 20)


def test_naive_datetimes_are_rejected():
    with pytest.raises(ValueError):
        booking_date(datetime(2026, 1, 15, 12, 0))


def test_parse_feed_timestamp_is_utc():
    assert parse_feed_timestamp("2026-01-15 22:30:00").tzinfo is UTC
