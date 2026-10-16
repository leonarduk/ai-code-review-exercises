"""Credit limits for corporate payment accounts."""

from decimal import Decimal


class CreditLimits:
    def __init__(self):
        self._limits: dict[str, Decimal] = {}

    def limit(self, account: str) -> Decimal:
        return self._limits.get(account, Decimal("0"))

    def set_limit(self, account: str, new_limit: Decimal) -> None:
        if new_limit < 0:
            raise ValueError("a credit limit cannot be negative")
        self._limits[account] = new_limit
