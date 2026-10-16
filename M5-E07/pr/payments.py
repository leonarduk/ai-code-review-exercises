"""Captured card payments, as the refund service sees them."""

from dataclasses import dataclass
from decimal import Decimal


@dataclass(frozen=True)
class Payment:
    payment_id: str
    merchant_id: str
    captured: Decimal
