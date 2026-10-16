from decimal import Decimal

import pytest

from approvals import ApprovalPolicy, UnknownCurrency
from money import Money

POLICY = ApprovalPolicy({"EUR": Decimal("1.09"), "GBP": Decimal("1.27"), "JPY": Decimal("0.0067")})


def usd(amount):
    return Money(Decimal(amount), "USD")


def test_large_payments_need_a_second_approver():
    assert POLICY.needs_second_approval(usd("15000.00")) is True
    assert POLICY.needs_second_approval(usd("10000.00")) is True


def test_small_payments_do_not():
    assert POLICY.needs_second_approval(usd("9999.99")) is False


def test_daily_limit_converts_to_usd():
    paid = [Money(Decimal("20000.00"), "EUR"), Money(Decimal("3000000"), "JPY")]
    assert POLICY.within_daily_limit(paid, usd("8000.00")) is True
    assert POLICY.within_daily_limit(paid, usd("8300.00")) is False


def test_unknown_currencies_are_rejected():
    with pytest.raises(UnknownCurrency):
        POLICY.to_usd(Money(Decimal("1.00"), "XYZ"))
