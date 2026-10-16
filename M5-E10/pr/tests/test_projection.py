import json
from decimal import Decimal

from projection import PaymentProjection


def event(sequence, kind, amount, payment_id="pay_1"):
    return json.dumps({"payment_id": payment_id, "sequence": sequence, "type": kind,
                       "amount": amount}).encode()


def test_events_in_order():
    view = PaymentProjection()
    view.receive(event(1, "authorised", 100.0))
    view.receive(event(2, "captured", 100.0))
    assert view.status("pay_1") == "captured"
    assert view.net_captured("pay_1") == Decimal("100.00")


def test_a_redelivered_event_is_ignored():
    view = PaymentProjection()
    view.receive(event(1, "authorised", 100.0))
    view.receive(event(2, "captured", 100.0))
    view.receive(event(2, "captured", 100.0))
    assert view.net_captured("pay_1") == Decimal("100.00")


def test_an_early_event_is_held_until_the_gap_is_filled():
    view = PaymentProjection()
    view.receive(event(2, "captured", 60.0))
    assert view.net_captured("pay_1") == Decimal("0")
    view.receive(event(1, "authorised", 60.0))
    assert view.net_captured("pay_1") == Decimal("60.00")


def test_partial_refund():
    view = PaymentProjection()
    for sequence, kind, amount in [(1, "authorised", 100.0), (2, "captured", 100.0), (3, "refunded", 25.5)]:
        view.receive(event(sequence, kind, amount))
    assert view.status("pay_1") == "refunded"
    assert view.net_captured("pay_1") == Decimal("74.50")
