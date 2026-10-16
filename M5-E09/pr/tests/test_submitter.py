from decimal import Decimal

from gateway import Instruction, RejectedError, TransientError
from submitter import PaymentSubmitter

PI_1 = Instruction("PI-1", "ACC-1", "GB29NWBK60161331926819", Decimal("250.00"))


class FakeGateway:
    def __init__(self, *outcomes):
        self.outcomes = list(outcomes)
        self.calls = 0

    def submit(self, instruction):
        self.calls += 1
        outcome = self.outcomes.pop(0) if self.outcomes else None
        if outcome is not None:
            raise outcome


def submitter(gateway, sleeps=None):
    pauses = [] if sleeps is None else sleeps
    return PaymentSubmitter(gateway, sleep=pauses.append, dead_letters=[])


def test_sent_first_time():
    gateway = FakeGateway()
    assert submitter(gateway).submit(PI_1) == "sent"
    assert gateway.calls == 1


def test_transient_failures_are_retried_with_backoff():
    sleeps = []
    gateway = FakeGateway(TransientError("timeout"), TransientError("timeout"))
    assert submitter(gateway, sleeps).submit(PI_1) == "sent"
    assert gateway.calls == 3
    assert sleeps == [1, 2]


def test_a_rejection_is_dead_lettered_without_retrying():
    gateway = FakeGateway(RejectedError("account closed"))
    s = submitter(gateway)
    assert s.submit(PI_1) == "dead-lettered"
    assert gateway.calls == 1
    assert [(d.instruction_id, d.attempts) for d in s.dead_letters] == [("PI-1", 1)]


def test_an_instruction_that_keeps_failing_is_dead_lettered():
    gateway = FakeGateway(*[TransientError("unavailable")] * 10)
    s = submitter(gateway)
    assert s.submit(PI_1) == "dead-lettered"
    assert s.dead_letters[0].instruction_id == "PI-1"
