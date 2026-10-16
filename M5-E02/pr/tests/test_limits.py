from datetime import datetime, timezone
from decimal import Decimal

import pytest

from limits import CreditLimits

T0 = datetime(2026, 5, 11, 9, 30, tzinfo=timezone.utc)


def test_new_accounts_have_no_credit():
    assert CreditLimits().limit("ACC-1") == Decimal("0")


def test_set_limit_is_audited():
    limits = CreditLimits(clock=lambda: T0)
    limits.set_limit("ACC-1", Decimal("5000.00"), "j.smith", "annual review")
    assert limits.limit("ACC-1") == Decimal("5000.00")
    [entry] = limits.audit_trail("ACC-1")
    assert (entry.at, entry.actor, entry.old_limit, entry.new_limit, entry.reason) == (
        T0, "j.smith", Decimal("0"), Decimal("5000.00"), "annual review")


def test_audit_trail_filters_by_account():
    limits = CreditLimits(clock=lambda: T0)
    limits.set_limit("ACC-1", Decimal("5000.00"), "j.smith", "annual review")
    limits.set_limit("ACC-2", Decimal("100.00"), "j.smith", "new client")
    assert [e.account for e in limits.audit_trail()] == ["ACC-1", "ACC-2"]
    assert [e.account for e in limits.audit_trail("ACC-2")] == ["ACC-2"]


def test_negative_limits_are_rejected():
    with pytest.raises(ValueError):
        CreditLimits().set_limit("ACC-1", Decimal("-1"), "j.smith", "typo")


def test_a_reason_is_required():
    with pytest.raises(ValueError):
        CreditLimits().set_limit("ACC-1", Decimal("10"), "j.smith", "  ")
