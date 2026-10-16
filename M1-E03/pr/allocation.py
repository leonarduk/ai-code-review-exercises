"""Pro-rata allocation of platform fees across client accounts."""

from __future__ import annotations

from dataclasses import dataclass
from decimal import Decimal, ROUND_HALF_EVEN
from typing import Mapping

CENT = Decimal("0.01")


class AllocationError(ValueError):
    """Raised when an amount cannot be allocated across the given balances."""


@dataclass(frozen=True)
class Allocation:
    account_id: str
    balance: Decimal
    amount: Decimal


def _to_cents(value: Decimal) -> Decimal:
    return value.quantize(CENT, rounding=ROUND_HALF_EVEN)


def allocate(total: Decimal, balances: Mapping[str, Decimal]) -> list[Allocation]:
    """Split ``total`` across accounts in proportion to their balances.

    Each share is rounded to the cent. Any rounding residue is assigned to the
    account with the largest balance (the first such account on a tie), so the
    allocated amounts always sum to ``total``.
    """
    if total != _to_cents(total):
        raise AllocationError(f"total {total} is not a whole number of cents")
    if not balances:
        raise AllocationError("no accounts to allocate across")
    if any(balance < 0 for balance in balances.values()):
        raise AllocationError("balances must not be negative")
    total_balance = sum(balances.values(), Decimal(0))
    if total_balance == 0:
        raise AllocationError("cannot allocate across a zero total balance")

    raw_shares = {
        account: total * balance / total_balance
        for account, balance in balances.items()
    }
    amounts = {account: _to_cents(share) for account, share in raw_shares.items()}

    residue = total - sum(raw_shares.values())
    largest = max(balances, key=balances.__getitem__)
    amounts[largest] += _to_cents(residue)

    return [
        Allocation(account, balances[account], amounts[account])
        for account in balances
    ]
