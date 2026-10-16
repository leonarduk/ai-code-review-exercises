"""Merchant service charges on card sales and refunds."""

from __future__ import annotations

from dataclasses import dataclass
from decimal import Decimal, ROUND_DOWN, ROUND_FLOOR

CENT = Decimal("0.01")
RATE = Decimal("0.0195")

SCHEME_RATES = {
    "VISA": Decimal("0.0195"),
    "MASTERCARD": Decimal("0.0195"),
    "AMEX": Decimal("0.0290"),
}
MINIMUM_FEE = Decimal("0.20")
MAXIMUM_FEE = Decimal("15.00")


def sale_fee(amount: Decimal) -> Decimal:
    """Fee on a card sale: 1.95% of the amount, truncated toward zero to the cent."""
    if amount <= 0:
        raise ValueError("sale amount must be positive")
    return (amount * RATE).quantize(CENT, rounding=ROUND_DOWN)


def scheme_rate(scheme: str) -> Decimal:
    try:
        return SCHEME_RATES[scheme.upper()]
    except KeyError:
        raise ValueError(f"unsupported card scheme {scheme!r}") from None


def _clamp(fee: Decimal) -> Decimal:
    """Bound the size of a signed fee by the minimum and maximum fee."""
    size = min(max(abs(fee), MINIMUM_FEE), MAXIMUM_FEE)
    return size.copy_sign(fee)


def transaction_fee(amount: Decimal, scheme: str) -> Decimal:
    """Fee on a card transaction: positive for a sale, negative for a refund."""
    if amount == 0:
        raise ValueError("transaction amount cannot be zero")
    fee = _clamp(amount * scheme_rate(scheme))
    return fee.quantize(CENT, rounding=ROUND_FLOOR)


@dataclass(frozen=True)
class Transaction:
    scheme: str
    amount: Decimal  # negative for a refund


def fees_due(transactions: list[Transaction]) -> Decimal:
    """Net fees for a settlement batch: fees on sales less fees refunded."""
    return sum((transaction_fee(t.amount, t.scheme) for t in transactions), Decimal("0.00"))
