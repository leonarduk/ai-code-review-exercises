from decimal import Decimal

import pytest

from forwards import Forward, converted_amount, load_tariff, settlement_amount

TARIFF = {"broker_fee": Decimal("25.00"), "clearing_fee": Decimal("1.50")}


def forward(notional, spot="1.0850", points="12.35", pair="EUR/USD"):
    return Forward(pair, Decimal(notional), Decimal(spot), Decimal(points))


def test_forward_rate_adds_points_in_pips():
    assert forward("1000000.00").forward_rate == Decimal("1.086235")


def test_discount_points_are_subtracted():
    assert forward("1000000.00", "1.2700", "-8.5", "GBP/USD").forward_rate == Decimal("1.26915")


@pytest.mark.parametrize(
    ("notional", "expected"),
    [
        ("1000000.00", "1086235.00"),
        ("250000.00", "271558.75"),
        ("12345.67", "13410.30"),
    ],
)
def test_converted_amount(notional, expected):
    assert converted_amount(forward(notional)) == Decimal(expected)


def test_settlement_adds_the_tariff_charges():
    assert settlement_amount(forward("1000000.00"), TARIFF) == Decimal("1086261.50")


def test_load_tariff_reads_decimals():
    tariff = load_tariff('{"broker_fee": 25.00, "clearing_fee": 1.50}')
    assert tariff == TARIFF
    assert all(type(fee) is Decimal for fee in tariff.values())


@pytest.mark.parametrize(
    "text",
    ['{"broker_fee": -1.00}', '{"broker_fee": 1.005}', '{"stamp_duty": 5.00}'],
)
def test_load_tariff_rejects_invalid_entries(text):
    with pytest.raises(ValueError):
        load_tariff(text)


def test_notional_must_be_positive():
    with pytest.raises(ValueError):
        converted_amount(forward("0.00"))
