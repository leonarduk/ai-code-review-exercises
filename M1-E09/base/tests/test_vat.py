from decimal import Decimal

import pytest

from vat import add_vat, vat_rate


def test_add_vat_standard_rate():
    assert add_vat(Decimal("10.00"), "S") == Decimal("12.00")


def test_add_vat_reduced_rate_rounds_half_even():
    assert add_vat(Decimal("0.50"), "R") == Decimal("0.52")


def test_unknown_code_is_rejected():
    with pytest.raises(ValueError):
        vat_rate("X")
