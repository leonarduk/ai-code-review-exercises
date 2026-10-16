"""Annual custody fee calculation.

Fees follow a tiered schedule loaded from ``fee_tiers.json``. The whole
holding value is charged at the rate of the tier it falls into, and the
result is rounded to the cent with banker's rounding.
"""

from __future__ import annotations

import json
from dataclasses import dataclass
from decimal import Decimal, ROUND_HALF_EVEN
from pathlib import Path
from typing import Optional

CENT = Decimal("0.01")
DEFAULT_SCHEDULE_PATH = Path(__file__).with_name("fee_tiers.json")


@dataclass(frozen=True)
class FeeTier:
    """A band of holding values charged at a single annual rate."""

    upper_limit: Optional[Decimal]  # None: no upper limit
    rate: Decimal


@dataclass(frozen=True)
class FeeSchedule:
    """An ordered set of fee tiers plus the minimum annual fee."""

    tiers: tuple[FeeTier, ...]
    minimum_fee: Decimal

    @classmethod
    def from_config(cls, config: dict) -> FeeSchedule:
        tiers = [
            FeeTier(
                upper_limit=None if tier["up_to"] is None else Decimal(tier["up_to"]),
                rate=Decimal(tier["rate"]),
            )
            for tier in config["tiers"]
        ]
        tiers.sort(key=lambda t: (t.upper_limit is None, t.upper_limit or 0))
        return cls(tiers=tuple(tiers), minimum_fee=Decimal(config["minimum_fee"]))

    @classmethod
    def load(cls, path: Path = DEFAULT_SCHEDULE_PATH) -> FeeSchedule:
        with path.open(encoding="utf-8") as handle:
            return cls.from_config(json.load(handle))

    def tier_for(self, holding_value: Decimal) -> FeeTier:
        for tier in self.tiers:
            if tier.upper_limit is None or holding_value < tier.upper_limit:
                return tier
        raise ValueError(f"no fee tier covers a holding value of {holding_value}")


def annual_fee(holding_value: Decimal, schedule: FeeSchedule | None = None) -> Decimal:
    """Annual custody fee for a holding, never less than the minimum fee."""
    if holding_value < 0:
        raise ValueError("holding value cannot be negative")
    schedule = schedule or FeeSchedule.load()
    tier = schedule.tier_for(holding_value)
    fee = (holding_value * tier.rate).quantize(CENT, rounding=ROUND_HALF_EVEN)
    return max(fee, schedule.minimum_fee)
