from decimal import Decimal

from money import round_cents


def test_round_cents_is_half_even():
    assert round_cents(Decimal("2.345")) == Decimal("2.34")
    assert round_cents(Decimal("2.355")) == Decimal("2.36")
