from decimal import Decimal

import pytest

from journal import GeneralLedger, JournalEntry, Line, Side, UnbalancedEntry


def line(account, side, amount, currency="GBP"):
    return Line(account, side, Decimal(amount), currency)


def test_balanced_entry_is_posted():
    gl = GeneralLedger()
    entry = JournalEntry("JE-1", (line("cash", Side.DEBIT, "120.00"), line("fees", Side.CREDIT, "100.00"),
                                  line("vat", Side.CREDIT, "20.00")))
    gl.post(entry)
    assert gl.entries == [entry]


def test_unbalanced_entry_is_rejected():
    gl = GeneralLedger()
    with pytest.raises(UnbalancedEntry):
        gl.post(JournalEntry("JE-2", (line("cash", Side.DEBIT, "120.00"), line("fees", Side.CREDIT, "100.00"))))
    assert gl.entries == []


def test_fx_trade_balances_in_each_currency():
    entry = JournalEntry("JE-3", (
        line("cash-usd", Side.DEBIT, "100.00", "USD"), line("fx-position-usd", Side.CREDIT, "100.00", "USD"),
        line("fx-position-eur", Side.DEBIT, "92.00", "EUR"), line("cash-eur", Side.CREDIT, "92.00", "EUR"),
    ))
    GeneralLedger().post(entry)


def test_single_line_and_negative_amounts_are_rejected():
    with pytest.raises(ValueError):
        GeneralLedger().post(JournalEntry("JE-4", (line("cash", Side.DEBIT, "1.00"),)))
    with pytest.raises(ValueError):
        GeneralLedger().post(JournalEntry("JE-5", (line("cash", Side.DEBIT, "-1.00"), line("fees", Side.CREDIT, "-1.00"))))
