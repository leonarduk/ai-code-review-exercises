from decimal import Decimal

from accrual import simple_interest


def test_simple_interest():
    assert simple_interest(Decimal("1000000.00"), Decimal("0.0365"), 10) == Decimal("1000.00")
