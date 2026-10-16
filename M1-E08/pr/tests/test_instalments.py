from decimal import Decimal

import pytest

from instalments import split_into_instalments, to_amount


def test_even_split():
    assert split_into_instalments(Decimal("300.00"), 3) == [Decimal("100.00")] * 3


def test_last_instalment_takes_the_remainder():
    assert split_into_instalments("100.00", 3) == [
        Decimal("33.33"),
        Decimal("33.33"),
        Decimal("33.34"),
    ]


def test_single_instalment_is_the_total():
    assert split_into_instalments(250, 1) == [Decimal("250")]


@pytest.mark.parametrize("value", ["12.345", "NaN", "Infinity", "abc"])
def test_to_amount_rejects_invalid_values(value):
    with pytest.raises(ValueError):
        to_amount(value)


def test_count_must_be_positive():
    with pytest.raises(ValueError):
        split_into_instalments("100.00", 0)
