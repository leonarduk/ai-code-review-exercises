from decimal import Decimal

import pytest

from fx import RateTable, UnknownCurrencyError, convert, to_usd

RATES = RateTable(
    {
        "EUR/USD": Decimal("1.0850"),
        "GBP/USD": Decimal("1.2700"),
        "USD/JPY": Decimal("151.20"),
        "USD/CHF": Decimal("0.8800"),
    }
)


def test_to_usd():
    assert to_usd(Decimal("100.00"), Decimal("1.0850")) == Decimal("108.50")


def test_converts_to_usd():
    assert convert(Decimal("1000.00"), "EUR", "USD", RATES) == Decimal("1085.00")


def test_cross_converts_through_usd():
    # 1,000 GBP = 1,270 USD = 1,170.5069... EUR
    assert convert(Decimal("1000.00"), "GBP", "EUR", RATES) == Decimal("1170.51")


def test_same_currency_is_returned_rounded():
    assert convert(Decimal("10.5"), "GBP", "GBP", RATES) == Decimal("10.50")


def test_unknown_currency_raises():
    with pytest.raises(UnknownCurrencyError):
        convert(Decimal("1"), "EUR", "AUD", RATES)
