import hashlib
import hmac
import json
from datetime import datetime, timedelta, timezone

from webhooks import Delivery, WebhookReceiver

SECRET = b"whsec_test"
T0 = datetime(2026, 9, 1, 12, 0, tzinfo=timezone.utc)


def delivery(key, at, event=None, secret=SECRET):
    body = json.dumps(event or {"type": "payment.settled", "id": key}).encode()
    return Delivery(key, at, body, hmac.new(secret, body, hashlib.sha256).hexdigest())


def receiver():
    handled = []
    return WebhookReceiver(SECRET, handled.append), handled


def test_first_delivery_is_handled():
    r, handled = receiver()
    assert r.receive(delivery("evt_1", T0)) == "accepted"
    assert handled == [{"type": "payment.settled", "id": "evt_1"}]


def test_retry_within_the_window_is_a_duplicate():
    r, handled = receiver()
    r.receive(delivery("evt_1", T0))
    assert r.receive(delivery("evt_1", T0 + timedelta(hours=5))) == "duplicate"
    assert len(handled) == 1


def test_keys_are_forgotten_after_the_window():
    r, handled = receiver()
    r.receive(delivery("evt_1", T0))
    assert r.receive(delivery("evt_1", T0 + timedelta(days=4))) == "accepted"


def test_bad_signature_is_rejected():
    r, handled = receiver()
    assert r.receive(delivery("evt_1", T0, secret=b"not-our-secret")) == "rejected"
    assert handled == []
