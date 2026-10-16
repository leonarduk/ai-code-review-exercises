"""Client interface to the payment gateway, as used by the submitter and its tests."""

from dataclasses import dataclass
from decimal import Decimal
from typing import Protocol


@dataclass(frozen=True)
class Instruction:
    instruction_id: str
    debit_account: str
    credit_iban: str
    amount: Decimal


class TransientError(Exception):
    """The gateway timed out or was unavailable. The same instruction may be sent again."""


class RejectedError(Exception):
    """The gateway refused the instruction. Sending it again will not change the answer."""


class Gateway(Protocol):
    def submit(self, instruction: Instruction) -> None:
        """Send one instruction. The gateway pays each instruction_id at most once."""
