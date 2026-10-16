from datetime import date

import pytest

from settlement import MarketCalendar, settlement_date


@pytest.mark.parametrize(
    ("trade_date", "cycle", "expected"),
    [
        (date(2026, 10, 13), 1, date(2026, 10, 14)),
        (date(2026, 10, 12), 2, date(2026, 10, 14)),
        (date(2026, 10, 16), 1, date(2026, 10, 19)),
        (date(2026, 10, 14), 0, date(2026, 10, 14)),
    ],
)
def test_settlement_date(trade_date, cycle, expected):
    assert settlement_date(trade_date, cycle) == expected


def test_negative_cycle_is_rejected():
    with pytest.raises(ValueError):
        settlement_date(date(2026, 10, 14), -1)


def test_calendar_loads_every_holiday():
    assert MarketCalendar.load().holiday_count == 10
