from datetime import date
from decimal import Decimal

from penalties import business_days_late, penalty

NO_HOLIDAYS: set[date] = set()


def test_one_day_late():
    assert business_days_late(date(2026, 10, 14), date(2026, 10, 15), NO_HOLIDAYS) == 1


def test_late_over_a_weekend():
    assert business_days_late(date(2026, 10, 16), date(2026, 10, 20), NO_HOLIDAYS) == 2


def test_settled_on_time():
    assert business_days_late(date(2026, 10, 14), date(2026, 10, 14), NO_HOLIDAYS) == 0


def test_penalty_amount():
    # 1,000,000.00 x 1 bp x 2 days = 200.00
    assert penalty(Decimal("1000000.00"), Decimal("1"), date(2026, 10, 16), date(2026, 10, 20), NO_HOLIDAYS) == Decimal("200.00")
