"""The payments view: each card payment's status and net captured amount."""

from decimal import Decimal


class PaymentProjection:
    def __init__(self):
        self._status: dict[str, str] = {}
        self._net: dict[str, Decimal] = {}

    def apply(self, payment_id: str, kind: str, amount: Decimal) -> None:
        """Apply one lifecycle event: authorised, captured or refunded."""
        self._status[payment_id] = kind
        if kind == "captured":
            self._net[payment_id] = self.net_captured(payment_id) + amount
        elif kind == "refunded":
            self._net[payment_id] = self.net_captured(payment_id) - amount

    def status(self, payment_id: str) -> str:
        return self._status[payment_id]

    def net_captured(self, payment_id: str) -> Decimal:
        """Captured less refunded; the settlement run pays this to the merchant."""
        return self._net.get(payment_id, Decimal("0"))
