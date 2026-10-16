from datetime import datetime, timedelta

import pytest

from accounts import AccountRegistry, Status, TransitionError


def registry():
    r = AccountRegistry()
    r.open("ACC-1")
    r.open("ACC-2")
    return r


def test_freeze_is_recorded():
    r = registry()
    r.change_status("ACC-1", Status.FROZEN, "k.patel")
    [change] = r.history("ACC-1")
    assert (change.old, change.new, change.actor) == (Status.ACTIVE, Status.FROZEN, "k.patel")
    assert datetime.utcnow() - change.at < timedelta(seconds=5)


def test_history_is_oldest_first_across_accounts():
    r = registry()
    r.change_status("ACC-1", Status.FROZEN, "k.patel")
    r.change_status("ACC-2", Status.CLOSED, "k.patel")
    r.change_status("ACC-1", Status.ACTIVE, "m.osei")
    assert [(c.account_id, c.new) for c in r.history()] == [
        ("ACC-1", Status.FROZEN), ("ACC-2", Status.CLOSED), ("ACC-1", Status.ACTIVE)]


def test_closed_accounts_cannot_be_reopened():
    r = registry()
    r.change_status("ACC-1", Status.CLOSED, "k.patel")
    with pytest.raises(TransitionError):
        r.change_status("ACC-1", Status.ACTIVE, "k.patel")


def test_changes_since():
    r = registry()
    r.change_status("ACC-1", Status.FROZEN, "k.patel")
    assert len(r.changes_since(datetime.utcnow() - timedelta(hours=1))) == 1
