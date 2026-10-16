from datetime import date
from decimal import Decimal

from money_market import Deposit


def test_deposit_holds_its_terms():
    deposit = Deposit(Decimal("1000000.00"), Decimal("0.036"), date(2024, 6, 1), date(2024, 7, 1))
    assert deposit.principal == Decimal("1000000.00")
