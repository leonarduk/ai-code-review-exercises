"""POST /refunds, idempotent on the Idempotency-Key header (MER-1033)."""

from dataclasses import dataclass
from decimal import Decimal

from payments import Payment


class UnknownPayment(Exception):
    """No such payment for this merchant."""


class RefundExceedsPayment(Exception):
    """The refund is more than is left to refund."""


class IdempotencyConflict(Exception):
    """The key was already used for a different request (HTTP 422)."""


@dataclass(frozen=True)
class RefundRequest:
    merchant_id: str
    payment_id: str
    amount: Decimal


@dataclass(frozen=True)
class RefundResponse:
    refund_id: str
    payment_id: str
    amount: Decimal
    remaining: Decimal


class RefundService:
    def __init__(self, payments: list[Payment]):
        self._payments = {p.payment_id: p for p in payments}
        self._refunded: dict[str, Decimal] = {}
        self._responses: dict[object, tuple[RefundRequest, RefundResponse]] = {}
        self._issued = 0

    def refund(self, idempotency_key: str, request: RefundRequest) -> RefundResponse:
        stored = self._responses.get(idempotency_key)
        if stored is not None:
            return stored[1]
        payment = self._payments.get(request.payment_id)
        if payment is None or payment.merchant_id != request.merchant_id:
            raise UnknownPayment(request.payment_id)
        if request.amount <= 0:
            raise ValueError("refund amount must be positive")
        remaining = payment.captured - self._refunded.get(payment.payment_id, Decimal("0"))
        if request.amount > remaining:
            raise RefundExceedsPayment(f"{request.amount} > {remaining}")
        self._refunded[payment.payment_id] = self._refunded.get(payment.payment_id, Decimal("0")) + request.amount
        self._issued += 1
        response = RefundResponse(f"rf_{self._issued}", payment.payment_id, request.amount,
                                  remaining - request.amount)
        self._responses[idempotency_key] = (request, response)
        return response
