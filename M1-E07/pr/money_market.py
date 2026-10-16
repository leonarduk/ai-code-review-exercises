"""Money-market deposits."""

from __future__ import annotations

from dataclasses import dataclass
from datetime import date
from decimal import Decimal, ROUND_HALF_EVEN

CENT = Decimal("0.01")
DAY_COUNT_BASIS = 360  # ACT/360


@dataclass(frozen=True)
class Deposit:
    principal: Decimal
    annual_rate: Decimal
    value_date: date
    maturity_date: date

    def __post_init__(self) -> None:
        if self.principal <= 0:
            raise ValueError("principal must be positive")
        if self.maturity_date <= self.value_date:
            raise ValueError("maturity date must be after the value date")

    @property
    def days(self) -> int:
        """Days in the deposit period: value date inclusive, maturity exclusive."""
        return (self.maturity_date - self.value_date).days

    def interest(self) -> Decimal:
        """Interest payable at maturity, ACT/360, rounded to the cent."""
        raw = self.principal * self.annual_rate * self.days / DAY_COUNT_BASIS
        return raw.quantize(CENT, rounding=ROUND_HALF_EVEN)

    def maturity_amount(self) -> Decimal:
        """Principal plus interest, as paid out at maturity."""
        return self.principal + self.interest()
