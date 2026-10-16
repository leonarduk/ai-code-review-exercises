from decimal import Decimal

from reconcile import reconcile

RATES = {"GBP": Decimal("1"), "EUR": Decimal("0.86"), "USD": Decimal("0.79")}


def test_matching_lines():
    report = reconcile(
        ["2026-09-01,INV-1,250.00,GBP", "2026-09-01,INV-2,100.00,EUR"],
        {"INV-1": Decimal("250.00"), "INV-2": Decimal("86.00")},
        RATES,
    )
    assert report.matched == ["INV-1", "INV-2"]
    assert report.unmatched_statement == report.unmatched_ledger == []


def test_amount_mismatch_is_unmatched_on_both_sides():
    report = reconcile(["2026-09-01,INV-1,249.00,GBP"], {"INV-1": Decimal("250.00")}, RATES)
    assert report.unmatched_statement == ["INV-1"]
    assert report.unmatched_ledger == ["INV-1"]


def test_unexpected_receipt_and_missing_receipt():
    report = reconcile(["2026-09-01,INV-9,10.00,USD"], {"INV-1": Decimal("250.00")}, RATES)
    assert report.unmatched_statement == ["INV-9"]
    assert report.unmatched_ledger == ["INV-1"]


def test_blank_lines_are_ignored():
    report = reconcile(["", "2026-09-01,INV-1,250.00,GBP", "  "], {"INV-1": Decimal("250.00")}, RATES)
    assert report.matched == ["INV-1"]
    assert report.malformed == []
