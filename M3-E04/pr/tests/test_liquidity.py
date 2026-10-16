from datetime import date
from decimal import Decimal

import pytest

from liquidity import daily_charge, hours_in_day, london_day_bounds


def test_an_ordinary_day_has_24_hours():
    assert hours_in_day(date(2026, 6, 1)) == Decimal(24)


def test_day_bounds_are_local_midnights():
    start, end = london_day_bounds(date(2026, 6, 1))
    assert (start.hour, end.hour, end.day) == (0, 0, 2)


def test_daily_charge_on_an_ordinary_day():
    # 8,760,000.00 x 5% x 24 / 8,760 = 1,200.00
    assert daily_charge(Decimal("8760000.00"), Decimal("0.05"), date(2026, 6, 1)) == Decimal("1200.00")


def test_negative_overdraft_is_rejected():
    with pytest.raises(ValueError):
        daily_charge(Decimal("-1"), Decimal("0.05"), date(2026, 6, 1))
