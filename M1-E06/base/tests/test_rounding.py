from decimal import Decimal

import pytest

from rounding import InvoiceLine, round_money


@pytest.mark.parametrize(
    ("amount", "expected"),
    [
        ("2.345", "2.35"),
        ("2.344", "2.34"),
        ("-2.345", "-2.35"),
        ("-2.344", "-2.34"),
        ("10", "10.00"),
    ],
)
def test_round_money(amount, expected):
    assert round_money(Decimal(amount)) == Decimal(expected)


def test_invoice_line():
    line = InvoiceLine("Custody fee", Decimal("1049.995"), Decimal("0.20"))
    assert line.tax == Decimal("210.00")
    assert line.gross == Decimal("1260.00")


def test_credit_note_line():
    line = InvoiceLine("Fee rebate", Decimal("-1049.995"), Decimal("0.20"))
    assert line.tax == Decimal("-210.00")
    assert line.gross == Decimal("-1260.00")
