from datetime import date, datetime, timezone

import pytest

from confirmations import from_epoch_millis, is_late, trade_date

# 2026-10-15 09:30:00.250 UTC
EXECUTED_MS = 1_792_056_600_250


def test_from_epoch_millis_keeps_the_milliseconds():
    assert from_epoch_millis(EXECUTED_MS) == datetime(2026, 10, 15, 9, 30, 0, 250_000, tzinfo=timezone.utc)


def test_booking_dates_are_returned_unchanged():
    assert trade_date(date(2026, 10, 15)) == date(2026, 10, 15)


def test_venue_timestamps_are_dated_in_tokyo():
    assert trade_date(EXECUTED_MS) == date(2026, 10, 15)


def test_unsupported_values_are_rejected():
    with pytest.raises(TypeError):
        trade_date("2026-10-15")


def test_confirmation_received_at_once_is_not_late():
    assert not is_late(EXECUTED_MS, EXECUTED_MS + 400)


def test_confirmation_received_20_minutes_later_is_late():
    assert is_late(EXECUTED_MS, EXECUTED_MS + 20 * 60 * 1000)
