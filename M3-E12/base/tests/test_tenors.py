from datetime import date

from tenors import modified_following


def test_business_day_is_unchanged():
    assert modified_following(date(2026, 4, 15), set()) == date(2026, 4, 15)


def test_weekend_rolls_forward():
    assert modified_following(date(2026, 4, 18), set()) == date(2026, 4, 20)


def test_month_end_weekend_rolls_back():
    assert modified_following(date(2026, 5, 30), set()) == date(2026, 5, 29)
