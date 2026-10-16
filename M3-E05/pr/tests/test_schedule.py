from datetime import date

import pytest

from schedule import is_month_end, monthly_schedule


def test_mid_month_schedule():
    assert monthly_schedule(date(2026, 3, 15), 3) == [
        date(2026, 3, 15), date(2026, 4, 15), date(2026, 5, 15),
    ]


def test_month_end_schedule_follows_month_ends():
    assert monthly_schedule(date(2026, 4, 30), 3) == [
        date(2026, 4, 30), date(2026, 5, 31), date(2026, 6, 30),
    ]


def test_is_month_end():
    assert is_month_end(date(2026, 2, 28))
    assert not is_month_end(date(2026, 3, 28))


def test_zero_payments():
    assert monthly_schedule(date(2026, 3, 15), 0) == []


def test_negative_count_is_rejected():
    with pytest.raises(ValueError):
        monthly_schedule(date(2026, 3, 15), -1)
