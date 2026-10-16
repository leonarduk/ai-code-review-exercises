from decimal import Decimal

from forwards import Forward


def test_forward_rate_adds_points_in_pips():
    forward = Forward("EUR/USD", Decimal("1000000.00"), Decimal("1.0850"), Decimal("12.35"))
    assert forward.forward_rate == Decimal("1.086235")


def test_discount_points_are_subtracted():
    forward = Forward("GBP/USD", Decimal("1000000.00"), Decimal("1.2700"), Decimal("-8.5"))
    assert forward.forward_rate == Decimal("1.26915")
