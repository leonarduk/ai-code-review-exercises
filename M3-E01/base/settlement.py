"""Settlement dates for cash equity trades."""

from datetime import date, timedelta


def settlement_date(trade_date: date) -> date:
    """T+1 settlement, ignoring weekends and holidays (interim, see SETT-88)."""
    return trade_date + timedelta(days=1)
