from datetime import date
from decimal import Decimal

import pytest

from money_market import Deposit


def deposit(principal="1000000.00", rate="0.036", start=date(2024, 6, 1), end=date(2024, 7, 1)):
    return Deposit(Decimal(principal), Decimal(rate), start, end)


def test_deposit_holds_its_terms():
    assert deposit().principal == Decimal("1000000.00")


def test_days_in_period():
    assert deposit().days == 30


def test_interest_act_360():
    assert deposit().interest() == Decimal("3000.00")


def test_maturity_amount():
    assert deposit().maturity_amount() == Decimal("1003000.00")


@pytest.mark.parametrize(
    ("principal", "end"),
    [("0", date(2024, 7, 1)), ("-5", date(2024, 7, 1)), ("100", date(2024, 6, 1))],
)
def test_invalid_deposits_are_rejected(principal, end):
    with pytest.raises(ValueError):
        deposit(principal=principal, end=end)
