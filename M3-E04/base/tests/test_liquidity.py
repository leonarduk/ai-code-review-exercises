from datetime import date
from decimal import Decimal

from liquidity import daily_charge


def test_daily_charge():
    assert daily_charge(Decimal("3650000.00"), Decimal("0.05"), date(2026, 6, 1)) == Decimal("500.00")
