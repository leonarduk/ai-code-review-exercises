from datetime import date

from business_days import following


def test_following_rolls_a_saturday_to_monday():
    assert following(date(2026, 10, 17), set()) == date(2026, 10, 19)
