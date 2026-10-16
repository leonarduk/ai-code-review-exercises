from decimal import Decimal

import pytest

from money import format_amount, parse_amount, round_amount


@pytest.mark.parametrize(
    ("amount", "currency", "expected"),
    [
        ("2.345", "USD", "2.34"),
        ("2.355", "EUR", "2.36"),
        ("1234.5", "JPY", "1234"),
        ("1.23456", "KWD", "1.235"),
    ],
)
def test_round_amount_uses_currency_minor_units(amount, currency, expected):
    assert round_amount(Decimal(amount), currency) == Decimal(expected)


def test_parse_amount_rounds_for_currency():
    assert parse_amount(" 1500.125 ", "GBP") == Decimal("1500.12")


def test_parse_amount_accepts_lower_case_currency():
    assert parse_amount("10", "chf") == Decimal("10.00")


@pytest.mark.parametrize(
    ("amount", "currency", "expected"),
    [
        ("1234567.891", "USD", "1,234,567.89 USD"),
        ("1234567.891", "KWD", "1,234,567.891 KWD"),
        ("1234567", "JPY", "1,234,567 JPY"),
    ],
)
def test_format_amount(amount, currency, expected):
    assert format_amount(Decimal(amount), currency) == expected
