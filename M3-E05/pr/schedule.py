"""Monthly repayment schedules for amortising loans."""

from __future__ import annotations

import calendar
from datetime import date


def is_month_end(day: date) -> bool:
    return day.day == calendar.monthrange(day.year, day.month)[1]


def monthly_schedule(first_payment: date, count: int) -> list[date]:
    """count monthly payment dates, starting with first_payment.

    Each payment falls on the first payment's day of the month, or on the
    last day of a shorter month. If the first payment is on a month end,
    every payment is on a month end.
    """
    if count < 0:
        raise ValueError(f"count must not be negative, got {count}")
    end_of_month = is_month_end(first_payment)
    payments = []
    for offset in range(count):
        month_index = first_payment.month + offset
        year = first_payment.year + month_index // 12
        month = (month_index - 1) % 12 + 1
        last_day = calendar.monthrange(year, month)[1]
        day = last_day if end_of_month else min(first_payment.day, 28)
        payments.append(date(year, month, day))
    return payments
