"""An amount of money in one currency."""

from dataclasses import dataclass
from decimal import Decimal


class CurrencyMismatch(Exception):
    """Arithmetic between amounts in different currencies."""


@dataclass(frozen=True, order=True)
class Money:
    amount: Decimal
    currency: str

    def __post_init__(self):
        if not isinstance(self.amount, Decimal):
            raise TypeError("amount must be a Decimal")
        if len(self.currency) != 3 or not self.currency.isupper():
            raise ValueError(f"not an ISO currency code: {self.currency!r}")

    def __add__(self, other: "Money") -> "Money":
        if other.currency != self.currency:
            raise CurrencyMismatch(f"{self.currency} + {other.currency}")
        return Money(self.amount + other.amount, self.currency)
