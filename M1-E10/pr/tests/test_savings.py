from decimal import Decimal

import pytest

from savings import MonthlyCredit, aer, annual_interest, project_savings, total_interest


def test_annual_interest():
    assert annual_interest(Decimal("1000.00"), Decimal("0.05")) == Decimal("50.00")


def test_annual_interest_rounds_half_even():
    assert annual_interest(Decimal("0.50"), Decimal("0.05")) == Decimal("0.02")


def test_negative_balance_is_rejected():
    with pytest.raises(ValueError):
        annual_interest(Decimal("-1.00"), Decimal("0.05"))


def test_first_months_of_a_projection():
    projection = project_savings(Decimal("1200.00"), Decimal("0.06"), 3)
    assert projection == [
        MonthlyCredit(1, Decimal("6.00"), Decimal("1206.00")),
        MonthlyCredit(2, Decimal("6.03"), Decimal("1212.03")),
        MonthlyCredit(3, Decimal("6.06"), Decimal("1218.09")),
    ]


def test_deposit_is_paid_in_after_the_interest():
    projection = project_savings(Decimal("0.00"), Decimal("0.06"), 2, Decimal("100.00"))
    assert [credit.interest for credit in projection] == [Decimal("0.00"), Decimal("0.50")]
    assert projection[-1].closing_balance == Decimal("200.50")


def test_total_interest_over_a_year():
    projection = project_savings(Decimal("1200.00"), Decimal("0.06"), 12)
    assert total_interest(projection) == Decimal("74.01")
    assert projection[-1].closing_balance == Decimal("1274.01")


def test_aer_of_monthly_compounding():
    assert aer(Decimal("0.06")) == Decimal("6.17")


@pytest.mark.parametrize(
    "kwargs",
    [
        {"opening_balance": Decimal("-1.00")},
        {"opening_balance": Decimal("1.005")},
        {"annual_rate": Decimal("-0.01")},
        {"months": -1},
        {"monthly_deposit": Decimal("-5.00")},
    ],
)
def test_invalid_inputs_are_rejected(kwargs):
    args = {"opening_balance": Decimal("100.00"), "annual_rate": Decimal("0.05"), "months": 1}
    with pytest.raises(ValueError):
        project_savings(**{**args, **kwargs})
