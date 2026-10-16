from datetime import date

import pytest

from tenors import maturity, modified_following


def test_business_day_is_unchanged():
    assert modified_following(date(2026, 4, 15), set()) == date(2026, 4, 15)


def test_weekend_rolls_forward():
    assert modified_following(date(2026, 4, 18), set()) == date(2026, 4, 20)


def test_month_end_weekend_rolls_back():
    assert modified_following(date(2026, 5, 30), set()) == date(2026, 5, 29)


@pytest.mark.parametrize(
    ("tenor", "expected"),
    [("1W", date(2026, 4, 22)), ("2W", date(2026, 4, 29)), ("1M", date(2026, 5, 15))],
)
def test_maturity_from_a_mid_month_spot(tenor, expected):
    assert maturity(date(2026, 4, 15), tenor) == expected


@pytest.mark.parametrize(
    ("tenor", "expected"),
    [("1M", date(2026, 5, 29)), ("3M", date(2026, 7, 31))],
)
def test_end_end_rule_from_a_month_end_spot(tenor, expected):
    assert maturity(date(2026, 4, 30), tenor) == expected


@pytest.mark.parametrize("tenor", ["", "3", "0M", "3D", "m3"])
def test_unsupported_tenors_are_rejected(tenor):
    with pytest.raises(ValueError):
        maturity(date(2026, 4, 15), tenor)
