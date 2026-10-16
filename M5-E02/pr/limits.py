"""Credit limits for corporate payment accounts, with an audit trail of every change (RISK-881)."""

from collections.abc import Callable
from dataclasses import dataclass
from datetime import datetime, timezone
from decimal import Decimal


@dataclass(frozen=True)
class AuditEntry:
    at: datetime
    actor: str
    account: str
    old_limit: Decimal
    new_limit: Decimal
    reason: str


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


class CreditLimits:
    def __init__(self, clock: Callable[[], datetime] = _utc_now):
        self._limits: dict[str, Decimal] = {}
        self._audit: list[AuditEntry] = []
        self._clock = clock

    def limit(self, account: str) -> Decimal:
        return self._limits.get(account, Decimal("0"))

    def set_limit(self, account: str, new_limit: Decimal, actor: str, reason: str) -> None:
        if new_limit < 0:
            raise ValueError("a credit limit cannot be negative")
        if not actor.strip() or not reason.strip():
            raise ValueError("every limit change needs an actor and a reason")
        at = self._clock()
        if at.tzinfo is None:
            raise ValueError("audit timestamps must be timezone-aware")
        self._audit.append(AuditEntry(at.astimezone(timezone.utc), actor, account, self.limit(account), new_limit, reason))
        self._limits[account] = new_limit

    def audit_trail(self, account: str | None = None) -> tuple[AuditEntry, ...]:
        """Every change, oldest first; only one account's if given."""
        return tuple(e for e in self._audit if account is None or e.account == account)
