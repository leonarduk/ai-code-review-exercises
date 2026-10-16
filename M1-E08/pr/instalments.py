"""Split an invoice total into equal instalments."""

from __future__ import annotations

from decimal import Decimal, InvalidOperation, ROUND_DOWN
from typing import Union

CENT = Decimal("0.01")
AmountLike = Union[Decimal, int, str]


def to_amount(value: AmountLike) -> Decimal:
    """Convert ``value`` to a Decimal amount in whole cents.

    Accepts Decimal, int or str. Floats are rejected rather than converted,
    because a float like 100.10 is not exactly 100.10; so is bool, which is
    an int subclass but never an amount.
    """
    if isinstance(value, (float, bool)):
        raise TypeError(f"pass amounts as Decimal, int or str, not {type(value).__name__}")
    try:
        amount = value if isinstance(value, Decimal) else Decimal(str(value))
    except InvalidOperation:
        raise ValueError(f"not a valid amount: {value!r}") from None
    if not amount.is_finite():
        raise ValueError(f"amount must be finite, got {value!r}")
    if amount != amount.quantize(CENT):
        raise ValueError(f"amount {value!r} has fractions of a cent")
    return amount


def split_into_instalments(total: AmountLike, count: int) -> list[Decimal]:
    """Split ``total`` into ``count`` instalments that sum exactly to ``total``.

    Every instalment except the last is ``total / count`` rounded toward zero
    to the cent; the last instalment takes whatever remains.
    """
    amount = to_amount(total)
    if count < 1:
        raise ValueError("count must be at least 1")
    regular = (amount / count).quantize(CENT, rounding=ROUND_DOWN)
    instalments = [regular] * (count - 1)
    instalments.append(amount - regular * (count - 1))
    return instalments
