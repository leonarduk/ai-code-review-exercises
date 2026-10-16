"""The payments view: each card payment's status and net captured amount.

Fed from the provider's event stream, which can deliver a payment's events
out of order and more than once (PAY-2417).
"""

import json
from dataclasses import dataclass
from decimal import Decimal


@dataclass(frozen=True)
class PaymentEvent:
    payment_id: str
    sequence: int
    kind: str
    amount: Decimal

    @classmethod
    def from_json(cls, body: bytes) -> "PaymentEvent":
        data = json.loads(body)
        return cls(data["payment_id"], int(data["sequence"]), data["type"], Decimal(data["amount"]))


class PaymentProjection:
    def __init__(self):
        self._status: dict[str, str] = {}
        self._net: dict[str, Decimal] = {}
        self._next: dict[str, int] = {}
        self._held: dict[str, dict[int, PaymentEvent]] = {}

    def receive(self, body: bytes) -> None:
        """Apply one event from the stream, in sequence order per payment."""
        event = PaymentEvent.from_json(body)
        expected = self._next.get(event.payment_id, 1)
        if event.sequence < expected:
            return
        held = self._held.setdefault(event.payment_id, {})
        held[event.sequence] = event
        if event.sequence == expected:
            for ready in held.values():
                self.apply(ready.payment_id, ready.kind, ready.amount)
            self._next[event.payment_id] = max(held) + 1
            held.clear()

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
