"""Submits payment instructions to the gateway."""

from gateway import Gateway, Instruction


class PaymentSubmitter:
    def __init__(self, gateway: Gateway):
        self._gateway = gateway

    def submit(self, instruction: Instruction) -> str:
        self._gateway.submit(instruction)
        return "sent"
