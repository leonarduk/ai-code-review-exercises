"""Money-market deposits."""

from dataclasses import dataclass
from datetime import date
from decimal import Decimal


@dataclass(frozen=True)
class Deposit:
    principal: Decimal
    annual_rate: Decimal
    value_date: date
    maturity_date: date
