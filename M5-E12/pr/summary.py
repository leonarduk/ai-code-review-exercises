"""End-of-day settlement summary per merchant, and the treasury copy net of fees (OPS-2450)."""

import copy
from collections.abc import Iterable
from decimal import ROUND_HALF_EVEN, Decimal

from settlement_file import Row

PENNY = Decimal("0.01")
TOP_N = 3


def summarise(rows: Iterable[Row], exclude: frozenset[str] = frozenset()) -> dict[str, dict]:
    """Per merchant: the number of payments, their total and the ids of the largest three."""
    rows = list(rows)
    summary: dict[str, dict] = {}
    for row in rows:
        if row.merchant in exclude:
            continue
        entry = summary.setdefault(row.merchant, {"count": 0, "total": Decimal("0.00"), "largest": []})
        entry["count"] += 1
        entry["total"] += row.amount
    for merchant, entry in summary.items():
        mine = [row for row in rows if row.merchant == merchant]
        by_size = sorted(mine, key=lambda row: row.amount, reverse=True)
        entry["largest"] = [row.payment_id for row in by_size[:TOP_N]]
    return summary


def net_of_fees(summary: dict[str, dict], fee_rates: dict[str, Decimal]) -> dict[str, dict]:
    """The treasury copy: each total less the merchant's fee. The summary itself is unchanged."""
    treasury = copy.deepcopy(summary)
    for merchant, entry in treasury.items():
        fee = (entry["total"] * fee_rates[merchant]).quantize(PENNY, rounding=ROUND_HALF_EVEN)
        entry["fee"] = fee
        entry["total"] -= fee
    return treasury
