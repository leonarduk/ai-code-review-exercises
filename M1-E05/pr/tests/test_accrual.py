from datetime import date
from decimal import Decimal

import pytest

from accrual import accrued_interest, daily_accruals, simple_interest

PRINCIPAL = Decimal("1000000.00")
RATE = Decimal("0.0365")  # 100.00 per day on ACT/365


def test_simple_interest():
    assert simple_interest(PRINCIPAL, RATE, 10) == Decimal("1000.00")


def test_one_accrual_per_day_of_january():
    accruals = list(daily_accruals(PRINCIPAL, RATE, date(2024, 1, 1), date(2024, 1, 31)))
    assert len(accruals) == 31
    assert accruals[0].accrual_date == date(2024, 1, 1)
    assert all(a.amount == Decimal("100") for a in accruals)


def test_accrued_interest_for_first_ten_days_of_march():
    assert accrued_interest(PRINCIPAL, RATE, date(2024, 3, 1), date(2024, 3, 10)) == Decimal("1000.00")


def test_end_before_start_is_rejected():
    with pytest.raises(ValueError):
        list(daily_accruals(PRINCIPAL, RATE, date(2024, 3, 10), date(2024, 3, 1)))
