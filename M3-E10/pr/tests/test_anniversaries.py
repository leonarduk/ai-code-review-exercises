from datetime import date

import pytest

from anniversaries import anniversary, completed_years, next_anniversary


def test_anniversary_keeps_month_and_day():
    assert anniversary(date(2023, 6, 15), 2026) == date(2026, 6, 15)


def test_leap_day_anniversary_in_a_leap_year():
    assert anniversary(date(2024, 2, 29), 2028) == date(2028, 2, 29)


@pytest.mark.parametrize(
    ("on", "years"),
    [
        (date(2025, 6, 15), 0),
        (date(2026, 6, 14), 0),
        (date(2026, 6, 15), 1),
        (date(2026, 12, 31), 1),
    ],
)
def test_completed_years(on, years):
    assert completed_years(date(2025, 6, 15), on) == years


def test_dates_before_opening_are_rejected():
    with pytest.raises(ValueError):
        completed_years(date(2025, 6, 15), date(2025, 6, 14))


def test_next_anniversary():
    assert next_anniversary(date(2023, 6, 15), date(2026, 3, 1)) == date(2026, 6, 15)
    assert next_anniversary(date(2023, 6, 15), date(2026, 6, 15)) == date(2027, 6, 15)
