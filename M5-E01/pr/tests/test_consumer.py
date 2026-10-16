from decimal import Decimal

import pytest

from consumer import Message, PaymentConsumer
from ledger import InsufficientFunds, Ledger


def message(offset, instruction_id="PI-1", amount="100.00"):
    return Message.from_record(0, offset, {
        "instruction_id": instruction_id, "debit": "ACC-1", "credit": "ACC-2", "amount": amount,
    })


def setup(balance="500.00"):
    ledger = Ledger({"ACC-1": Decimal(balance), "ACC-2": Decimal("0.00")})
    return ledger, PaymentConsumer(ledger)


def test_instruction_is_posted():
    ledger, consumer = setup()
    assert consumer.handle(message(7)) is True
    assert ledger.balance("ACC-1") == Decimal("400.00")
    assert ledger.balance("ACC-2") == Decimal("100.00")


def test_redelivery_is_not_posted_twice():
    ledger, consumer = setup()
    consumer.handle(message(7))
    assert consumer.handle(message(7)) is False
    assert ledger.balance("ACC-1") == Decimal("400.00")


def test_insufficient_funds_is_raised_for_retry_and_recorded():
    ledger, consumer = setup(balance="50.00")
    with pytest.raises(InsufficientFunds):
        consumer.handle(message(9, "PI-2"))
    assert consumer.rejected == ["PI-2"]
    assert ledger.balance("ACC-1") == Decimal("50.00")
