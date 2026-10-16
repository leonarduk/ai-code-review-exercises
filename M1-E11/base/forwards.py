"""FX forward contracts."""

from dataclasses import dataclass
from decimal import Decimal

PIP = Decimal("0.0001")


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
