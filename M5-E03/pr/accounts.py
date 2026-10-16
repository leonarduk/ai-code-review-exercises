"""Payment account lifecycle, with a status history for the audit log (CMP-412)."""

from dataclasses import dataclass
from datetime import datetime
from enum import Enum


class Status(Enum):
    ACTIVE = "active"
    FROZEN = "frozen"
    CLOSED = "closed"


ALLOWED = {
    Status.ACTIVE: {Status.FROZEN, Status.CLOSED},
    Status.FROZEN: {Status.ACTIVE, Status.CLOSED},
    Status.CLOSED: set(),
}


class TransitionError(Exception):
    """A status change the lifecycle does not allow."""


@dataclass(frozen=True)
class StatusChange:
    at: datetime
    account_id: str
    old: Status
    new: Status
    actor: str


class AccountRegistry:
    def __init__(self):
        self._status: dict[str, Status] = {}
        self._history: list[StatusChange] = []

    def open(self, account_id: str) -> None:
        if account_id in self._status:
            raise ValueError(f"{account_id} already exists")
        self._status[account_id] = Status.ACTIVE

    def status(self, account_id: str) -> Status:
        return self._status[account_id]

    def change_status(self, account_id: str, new: Status, actor: str) -> None:
        old = self._status[account_id]
        if new not in ALLOWED[old]:
            raise TransitionError(f"{account_id}: {old.value} -> {new.value}")
        self._history.append(StatusChange(datetime.utcnow(), account_id, old, new, actor))
        self._status[account_id] = new

    def history(self, account_id: str | None = None) -> list[StatusChange]:
        """Status changes, oldest first: all of them, or one account's."""
        if account_id is None:
            return self._history
        return [c for c in self._history if c.account_id == account_id]

    def changes_since(self, since: datetime) -> list[StatusChange]:
        """Changes at or after since, for the hourly feed to the compliance archive."""
        return [c for c in self._history if c.at >= since]
