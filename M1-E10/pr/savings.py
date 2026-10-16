"""Savings account interest."""

from __future__ import annotations

from dataclasses import dataclass
from decimal import Decimal, ROUND_HALF_EVEN

CENT = Decimal("0.01")
MONTHS_PER_YEAR = 12


def annual_interest(balance: Decimal, annual_rate: Decimal) -> Decimal:
    """Simple interest for a year on a constant balance, rounded half-even to the cent."""
    if balance < 0 or annual_rate < 0:
        raise ValueError("balance and rate must be non-negative")
    return (balance * annual_rate).quantize(CENT, rounding=ROUND_HALF_EVEN)


@dataclass(frozen=True)
class MonthlyCredit:
    month: int
    interest: Decimal
    closing_balance: Decimal


def _whole_cents(name: str, value: Decimal) -> Decimal:
    if not value.is_finite() or value < 0 or value != value.quantize(CENT):
        raise ValueError(f"{name} must be a non-negative whole number of cents, got {value}")
    return value


def project_savings(
    opening_balance: Decimal,
    annual_rate: Decimal,
    months: int,
    monthly_deposit: Decimal = Decimal("0.00"),
) -> list[MonthlyCredit]:
    """Month-by-month projection of a savings account with monthly compounding.

    Each month's interest is rounded to the cent and credited, as the ledger
    posts it; the deposit is paid in after the interest is credited.
    """
    balance = _whole_cents("opening balance", opening_balance)
    deposit = _whole_cents("monthly deposit", monthly_deposit)
    if not annual_rate.is_finite() or annual_rate < 0:
        raise ValueError(f"rate must be finite and non-negative, got {annual_rate}")
    if months < 0:
        raise ValueError("months must be non-negative")
    projection = []
    for month in range(1, months + 1):
        interest = (balance * annual_rate / MONTHS_PER_YEAR).quantize(CENT, rounding=ROUND_HALF_EVEN)
        balance = balance + interest + deposit
        projection.append(MonthlyCredit(month, interest, balance))
    return projection


def total_interest(projection: list[MonthlyCredit]) -> Decimal:
    """Interest credited over the whole projection."""
    return sum((credit.interest for credit in projection), Decimal("0.00"))


def aer(annual_rate: Decimal) -> Decimal:
    """Annual equivalent rate of monthly compounding, in percent to 2 places."""
    if not annual_rate.is_finite() or annual_rate < 0:
        raise ValueError(f"rate must be finite and non-negative, got {annual_rate}")
    growth = (1 + annual_rate / MONTHS_PER_YEAR) ** MONTHS_PER_YEAR
    return ((growth - 1) * 100).quantize(CENT, rounding=ROUND_HALF_EVEN)
