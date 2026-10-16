from decimal import Decimal

import pytest

from savings import annual_interest


def test_annual_interest():
    assert annual_interest(Decimal("1000.00"), Decimal("0.05")) == Decimal("50.00")


def test_annual_interest_rounds_half_even():
    assert annual_interest(Decimal("0.50"), Decimal("0.05")) == Decimal("0.02")


def test_negative_balance_is_rejected():
    with pytest.raises(ValueError):
        annual_interest(Decimal("-1.00"), Decimal("0.05"))
