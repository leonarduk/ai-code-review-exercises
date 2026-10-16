"""Booking date for client orders received around the daily cut-off."""

from datetime import date, datetime, time, timedelta

CUT_OFF_UTC = time(22, 0)


def booking_date(received_at_utc: datetime) -> date:
    """Orders received at or after 22:00 UTC are booked the next day."""
    day = received_at_utc.date()
    if received_at_utc.time() >= CUT_OFF_UTC:
        day += timedelta(days=1)
    return day
