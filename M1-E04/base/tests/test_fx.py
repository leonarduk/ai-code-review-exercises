from decimal import Decimal

from fx import to_usd


def test_to_usd():
    assert to_usd(Decimal("100.00"), Decimal("1.0850")) == Decimal("108.50")
