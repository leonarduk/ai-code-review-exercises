from decimal import Decimal

import pytest

from merchant_fees import Transaction, fees_due, sale_fee, transaction_fee


def test_fee_is_truncated_to_the_cent():
    assert sale_fee(Decimal("100.00")) == Decimal("1.95")
    assert sale_fee(Decimal("10.37")) == Decimal("0.20")


def test_non_positive_sale_is_rejected():
    with pytest.raises(ValueError):
        sale_fee(Decimal("0.00"))


@pytest.mark.parametrize(
    ("amount", "scheme", "expected"),
    [
        ("100.00", "VISA", "1.95"),
        ("57.89", "mastercard", "1.12"),
        ("250.00", "AMEX", "7.25"),
        ("5.00", "VISA", "0.20"),
        ("2000.00", "AMEX", "15.00"),
    ],
)
def test_sale_fees(amount, scheme, expected):
    assert transaction_fee(Decimal(amount), scheme) == Decimal(expected)


def test_refund_reverses_the_fee():
    assert transaction_fee(Decimal("-100.00"), "VISA") == Decimal("-1.95")
    assert transaction_fee(Decimal("-2000.00"), "AMEX") == Decimal("-15.00")


def test_batch_nets_refunds_against_sales():
    batch = [
        Transaction("VISA", Decimal("100.00")),
        Transaction("AMEX", Decimal("250.00")),
        Transaction("VISA", Decimal("-100.00")),
    ]
    assert fees_due(batch) == Decimal("7.25")


@pytest.mark.parametrize(("amount", "scheme"), [("0.00", "VISA"), ("10.00", "DINERS")])
def test_invalid_transactions_are_rejected(amount, scheme):
    with pytest.raises(ValueError):
        transaction_fee(Decimal(amount), scheme)
