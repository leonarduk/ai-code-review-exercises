"""Receives payment-provider webhooks."""

import hashlib
import hmac
import json
from collections.abc import Callable


class WebhookReceiver:
    def __init__(self, secret: bytes, handler: Callable[[dict], None]):
        self._secret = secret
        self._handler = handler

    def _verify(self, body: bytes, signature: str) -> bool:
        expected = hmac.new(self._secret, body, hashlib.sha256).hexdigest()
        return hmac.compare_digest(expected, signature)

    def receive(self, body: bytes, signature: str) -> str:
        if not self._verify(body, signature):
            return "rejected"
        self._handler(json.loads(body))
        return "accepted"
