import random
from decimal import Decimal

import pytest

from rounding import InvoiceLine, round_money

SAMPLE_AMOUNTS = [
    "0",
    "0.004",
    "0.005",
    "2.344",
    "2.345",
    "10",
    "1049.995",
    "-2.345",
    "-0.005",
    "123456789.125",
]


def expected_rounding(amount: Decimal) -> Decimal:
    return Decimal(int(amount * 100 + Decimal("0.5"))).scaleb(-2)


@pytest.mark.parametrize("raw", SAMPLE_AMOUNTS)
def test_round_money(raw):
    amount = Decimal(raw)
    assert round_money(amount) == expected_rounding(amount)


@pytest.mark.parametrize("raw", SAMPLE_AMOUNTS)
def test_round_money_returns_whole_cents(raw):
    assert round_money(Decimal(raw)).as_tuple().exponent == -2


def test_round_money_on_random_amounts():
    rng = random.Random(42)
    for _ in range(1000):
        amount = Decimal(rng.randint(-10**8, 10**8)).scaleb(-3)
        assert round_money(amount) == expected_rounding(amount)


@pytest.mark.parametrize("net", ["1049.995", "-1049.995", "0.01"])
def test_invoice_line_totals(net):
    line = InvoiceLine("Custody fee", Decimal(net), Decimal("0.20"))
    assert line.tax == expected_rounding(Decimal(net) * Decimal("0.20"))
    assert line.gross == expected_rounding(Decimal(net)) + line.tax
