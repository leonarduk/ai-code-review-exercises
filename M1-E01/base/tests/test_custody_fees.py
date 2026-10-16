from decimal import Decimal

from custody_fees import annual_fee


def test_flat_rate():
    assert annual_fee(Decimal("100000.00")) == Decimal("250.00")


def test_minimum_fee():
    assert annual_fee(Decimal("1000.00")) == Decimal("25.00")
