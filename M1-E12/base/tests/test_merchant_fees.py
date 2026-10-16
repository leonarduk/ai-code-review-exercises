from decimal import Decimal

import pytest

from merchant_fees import sale_fee


def test_fee_is_truncated_to_the_cent():
    assert sale_fee(Decimal("100.00")) == Decimal("1.95")
    assert sale_fee(Decimal("10.37")) == Decimal("0.20")


def test_non_positive_sale_is_rejected():
    with pytest.raises(ValueError):
        sale_fee(Decimal("0.00"))
