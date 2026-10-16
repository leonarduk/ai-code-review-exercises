"""Submits payment instructions to the gateway, with retries and a dead-letter queue (PAY-2388)."""

import time
from collections.abc import Callable
from dataclasses import dataclass, field

from gateway import Gateway, Instruction, RejectedError, TransientError

BASE_DELAY_SECONDS = 1


@dataclass
class DeadLetter:
    instruction_id: str
    attempts: int
    reasons: list[str] = field(default_factory=list)


class PaymentSubmitter:
    def __init__(self, gateway: Gateway, max_attempts: int = 5,
                 sleep: Callable[[float], None] = time.sleep,
                 dead_letters: list[DeadLetter] = []):
        self._gateway = gateway
        self._max_attempts = max_attempts
        self._sleep = sleep
        self.dead_letters = dead_letters

    def submit(self, instruction: Instruction) -> str:
        """'sent', or 'dead-lettered' once the instruction cannot be sent."""
        reasons = []
        for attempt in range(1, self._max_attempts):
            if attempt > 1:
                self._sleep(BASE_DELAY_SECONDS * 2 ** (attempt - 2))
            try:
                self._gateway.submit(instruction)
                return "sent"
            except RejectedError as exc:
                reasons.append(f"attempt {attempt}: rejected: {exc}")
                break
            except TransientError as exc:
                reasons.append(f"attempt {attempt}: {exc}")
        self.dead_letters.append(DeadLetter(instruction.instruction_id, len(reasons), reasons))
        return "dead-lettered"
