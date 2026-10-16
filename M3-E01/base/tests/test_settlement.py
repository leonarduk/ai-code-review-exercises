from datetime import date

from settlement import settlement_date


def test_next_day():
    assert settlement_date(date(2026, 10, 13)) == date(2026, 10, 14)
