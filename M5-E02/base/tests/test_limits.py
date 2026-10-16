from decimal import Decimal

import pytest

from limits import CreditLimits


def test_new_accounts_have_no_credit():
    assert CreditLimits().limit("ACC-1") == Decimal("0")


def test_set_limit():
    limits = CreditLimits()
    limits.set_limit("ACC-1", Decimal("5000.00"))
    assert limits.limit("ACC-1") == Decimal("5000.00")


def test_negative_limits_are_rejected():
    with pytest.raises(ValueError):
        CreditLimits().set_limit("ACC-1", Decimal("-1"))
