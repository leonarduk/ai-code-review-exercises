"""Payment account lifecycle."""

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


class AccountRegistry:
    def __init__(self):
        self._status: dict[str, Status] = {}

    def open(self, account_id: str) -> None:
        if account_id in self._status:
            raise ValueError(f"{account_id} already exists")
        self._status[account_id] = Status.ACTIVE

    def status(self, account_id: str) -> Status:
        return self._status[account_id]

    def change_status(self, account_id: str, new: Status) -> None:
        old = self._status[account_id]
        if new not in ALLOWED[old]:
            raise TransitionError(f"{account_id}: {old.value} -> {new.value}")
        self._status[account_id] = new
