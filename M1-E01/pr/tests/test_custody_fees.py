from decimal import Decimal

import pytest

from custody_fees import FeeSchedule, annual_fee

SCHEDULE = FeeSchedule.from_config(
    {
        "minimum_fee": "25.00",
        "tiers": [
            {"up_to": 100000, "rate": 0.003},
            {"up_to": 1000000, "rate": 0.002},
            {"up_to": None, "rate": 0.001},
        ],
    }
)


@pytest.mark.parametrize(
    ("holding", "expected"),
    [
        ("50000.00", "150.00"),
        ("250000.00", "500.00"),
        ("2500000.00", "2500.00"),
    ],
)
def test_fee_uses_the_rate_of_the_tier_the_holding_falls_into(holding, expected):
    assert annual_fee(Decimal(holding), SCHEDULE) == Decimal(expected)


def test_minimum_fee_applies_to_small_holdings():
    assert annual_fee(Decimal("1000.00"), SCHEDULE) == Decimal("25.00")


def test_negative_holding_is_rejected():
    with pytest.raises(ValueError):
        annual_fee(Decimal("-1"), SCHEDULE)


def test_default_schedule_loads_from_json():
    schedule = FeeSchedule.load()
    assert schedule.minimum_fee == Decimal("25.00")
    assert len(schedule.tiers) == 3
