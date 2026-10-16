from decimal import Decimal

from settlement_file import parse
from summary import net_of_fees, summarise

FILE = [
    "merchant,payment_id,amount",
    "m_acme,P1,120.00",
    "m_acme,P2,80.00",
    "m_brix,P3,45.50",
]


def test_totals_and_counts_per_merchant():
    summary = summarise(parse(FILE))
    assert summary["m_acme"]["count"] == 2
    assert summary["m_acme"]["total"] == Decimal("200.00")
    assert summary["m_brix"]["total"] == Decimal("45.50")


def test_largest_payments_first():
    summary = summarise(parse(FILE))
    assert summary["m_acme"]["largest"] == ["P1", "P2"]


def test_excluded_merchants_are_left_out():
    assert list(summarise(parse(FILE), exclude=frozenset({"m_brix"}))) == ["m_acme"]


def test_treasury_copy_is_net_of_fees():
    summary = summarise(parse(FILE))
    treasury = net_of_fees(summary, {"m_acme": Decimal("0.015"), "m_brix": Decimal("0.02")})
    assert (treasury["m_acme"]["fee"], treasury["m_acme"]["total"]) == (Decimal("3.00"), Decimal("197.00"))
    assert treasury["m_brix"]["total"] == Decimal("44.59")
