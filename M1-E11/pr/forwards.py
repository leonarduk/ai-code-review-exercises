"""FX forward contracts and their settlement amounts."""

from __future__ import annotations

import json
from dataclasses import dataclass
from decimal import Decimal, ROUND_HALF_EVEN, localcontext

PIP = Decimal("0.0001")
CENT = Decimal("0.01")
FEE_NAMES = ("broker_fee", "clearing_fee")
RATE_PRECISION = 10  # significant digits the rate feed quotes to


@dataclass(frozen=True)
class Forward:
    pair: str  # BASE/QUOTE, e.g. "EUR/USD"
    notional: Decimal  # in the base currency
    spot: Decimal
    forward_points: Decimal  # in pips; negative when the base trades at a discount

    @property
    def forward_rate(self) -> Decimal:
        """All-in forward rate: spot plus the forward points."""
        return self.spot + self.forward_points * PIP


def load_tariff(text: str) -> dict[str, Decimal]:
    """Parse a client's settlement tariff from JSON, reading amounts as Decimal."""
    tariff = json.loads(text, parse_float=Decimal)
    unknown = sorted(set(tariff) - set(FEE_NAMES))
    if unknown:
        raise ValueError(f"unknown tariff entries: {unknown}")
    for name, fee in tariff.items():
        if fee < 0 or fee != Decimal(fee).quantize(CENT):
            raise ValueError(f"{name} must be a non-negative whole number of cents, got {fee}")
    return tariff


def converted_amount(forward: Forward) -> Decimal:
    """The notional converted at the forward rate, in the quote currency."""
    if forward.notional <= 0:
        raise ValueError("notional must be positive")
    with localcontext() as ctx:
        ctx.prec = RATE_PRECISION
        rate = forward.forward_rate
        amount = forward.notional * rate
    return amount.quantize(CENT, rounding=ROUND_HALF_EVEN)


def settlement_amount(forward: Forward, tariff: dict[str, Decimal]) -> Decimal:
    """What the client pays at settlement: the converted amount plus charges."""
    charges = sum(tariff.get(name, 0.0) for name in FEE_NAMES)
    return converted_amount(forward) + charges
