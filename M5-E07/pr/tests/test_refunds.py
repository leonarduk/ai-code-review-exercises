from decimal import Decimal

import pytest

from payments import Payment
from refunds import RefundExceedsPayment, RefundRequest, RefundService, UnknownPayment


def service():
    return RefundService([Payment("pay_1", "m_acme", Decimal("80.00"))])


def test_refund():
    response = service().refund("key-1", RefundRequest("m_acme", "pay_1", Decimal("30.00")))
    assert (response.refund_id, response.amount, response.remaining) == ("rf_1", Decimal("30.00"), Decimal("50.00"))


def test_retry_returns_the_original_response_without_refunding_again():
    s = service()
    request = RefundRequest("m_acme", "pay_1", Decimal("30.00"))
    first = s.refund("key-1", request)
    assert s.refund("key-1", request) == first
    assert s.refund("key-2", RefundRequest("m_acme", "pay_1", Decimal("50.00"))).remaining == Decimal("0.00")


def test_refund_cannot_exceed_what_is_left():
    s = service()
    s.refund("key-1", RefundRequest("m_acme", "pay_1", Decimal("60.00")))
    with pytest.raises(RefundExceedsPayment):
        s.refund("key-2", RefundRequest("m_acme", "pay_1", Decimal("20.01")))


def test_unknown_payment():
    with pytest.raises(UnknownPayment):
        service().refund("key-1", RefundRequest("m_acme", "pay_404", Decimal("1.00")))
