"""Receives payment-provider webhooks, once per idempotency key (PAY-2340)."""

import hashlib
import hmac
import json
from collections.abc import Callable
from dataclasses import dataclass
from datetime import datetime, timedelta

RETENTION = timedelta(hours=72)


@dataclass(frozen=True)
class Delivery:
    key: str
    received_at: datetime
    body: bytes
    signature: str


class WebhookReceiver:
    def __init__(self, secret: bytes, handler: Callable[[dict], None]):
        self._secret = secret
        self._handler = handler
        self._seen: dict[str, datetime] = {}

    def _verify(self, body: bytes, signature: str) -> bool:
        expected = hmac.new(self._secret, body, hashlib.sha256).hexdigest()
        return hmac.compare_digest(expected, signature)

    def receive(self, delivery: Delivery) -> str:
        """'accepted', 'duplicate' or 'rejected'."""
        if delivery.received_at.tzinfo is None:
            raise ValueError("received_at must be timezone-aware")
        if not self._verify(delivery.body, delivery.signature):
            return "rejected"
        self._forget_before(delivery.received_at - RETENTION)
        if delivery.key in self._seen:
            return "duplicate"
        self._handler(json.loads(delivery.body))
        self._seen[delivery.key] = delivery.received_at
        return "accepted"

    def _forget_before(self, cutoff: datetime) -> None:
        for key, seen_at in list(self._seen.items()):
            if seen_at < cutoff:
                del self._seen[key]
