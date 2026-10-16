from decimal import Decimal

import pytest

from allocation import AllocationError, allocate


def amounts(allocations):
    return {a.account_id: a.amount for a in allocations}


def test_allocates_in_proportion_to_balance():
    result = allocate(
        Decimal("1000.00"), {"ACC-1": Decimal("600000"), "ACC-2": Decimal("400000")}
    )
    assert amounts(result) == {"ACC-1": Decimal("600.00"), "ACC-2": Decimal("400.00")}


@pytest.mark.parametrize(
    ("total", "balances"),
    [
        ("100.00", {"A": "1", "B": "3"}),
        ("250.00", {"A": "10", "B": "10", "C": "30"}),
        ("0.00", {"A": "5", "B": "7"}),
    ],
)
def test_allocations_sum_to_total(total, balances):
    result = allocate(Decimal(total), {k: Decimal(v) for k, v in balances.items()})
    assert sum(a.amount for a in result) == Decimal(total)


def test_result_preserves_input_order():
    result = allocate(Decimal("10.00"), {"Z": Decimal("1"), "A": Decimal("1")})
    assert [a.account_id for a in result] == ["Z", "A"]


@pytest.mark.parametrize(
    ("total", "balances"),
    [
        ("10.005", {"A": Decimal("1")}),
        ("10.00", {}),
        ("10.00", {"A": Decimal("-1"), "B": Decimal("2")}),
        ("10.00", {"A": Decimal("0"), "B": Decimal("0")}),
    ],
)
def test_invalid_input_is_rejected(total, balances):
    with pytest.raises(AllocationError):
        allocate(Decimal(total), balances)
