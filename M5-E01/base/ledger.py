"""In-memory view of the payments ledger, as used by the consumers and their tests."""

from decimal import Decimal


class InsufficientFunds(Exception):
    """The debit account cannot cover the transfer."""


class Ledger:
    def __init__(self, balances: dict[str, Decimal]):
        self._balances = dict(balances)

    def balance(self, account: str) -> Decimal:
        return self._balances[account]

    def deposit(self, account: str, amount: Decimal) -> None:
        if amount <= 0:
            raise ValueError("deposit must be positive")
        self._balances[account] += amount

    def transfer(self, debit: str, credit: str, amount: Decimal) -> None:
        """Move amount from debit to credit; nothing changes if it raises."""
        if amount <= 0:
            raise ValueError("transfer must be positive")
        if self._balances[debit] < amount:
            raise InsufficientFunds(debit)
        self._balances[debit] -= amount
        self._balances[credit] += amount
