from decimal import Decimal

import pytest

from vat import ReceiptLine, VatRow, add_vat, extract_vat, total_vat, vat_rate, vat_summary


def test_add_vat_standard_rate():
    assert add_vat(Decimal("10.00"), "S") == Decimal("12.00")


def test_add_vat_reduced_rate_rounds_half_even():
    assert add_vat(Decimal("0.50"), "R") == Decimal("0.52")


def test_unknown_code_is_rejected():
    with pytest.raises(ValueError):
        vat_rate("X")


@pytest.mark.parametrize(
    ("gross", "code", "net", "vat"),
    [
        ("12.00", "S", "10.00", "2.00"),
        ("4.99", "S", "4.16", "0.83"),
        ("21.00", "R", "20.00", "1.00"),
        ("3.50", "Z", "3.50", "0.00"),
    ],
)
def test_extract_vat_splits_a_vat_inclusive_amount(gross, code, net, vat):
    assert extract_vat(Decimal(gross), code) == (Decimal(net), Decimal(vat))


def test_extracted_parts_sum_to_the_gross():
    for cents in range(1, 2000, 7):
        gross = Decimal(cents).scaleb(-2)
        net, vat = extract_vat(gross, "S")
        assert net + vat == gross


def test_summary_has_one_row_per_code_in_code_order():
    lines = [
        ReceiptLine("Bread", Decimal("1.40"), 1, "Z"),
        ReceiptLine("Batteries", Decimal("6.00"), 2, "S"),
        ReceiptLine("Child seat", Decimal("42.00"), 1, "R"),
    ]
    assert vat_summary(lines) == [
        VatRow("S", Decimal("0.20"), Decimal("12.00"), Decimal("10.00"), Decimal("2.00")),
        VatRow("R", Decimal("0.05"), Decimal("42.00"), Decimal("40.00"), Decimal("2.00")),
        VatRow("Z", Decimal("0.00"), Decimal("1.40"), Decimal("1.40"), Decimal("0.00")),
    ]
    assert total_vat(lines) == Decimal("4.00")


def test_empty_receipt_has_no_vat():
    assert vat_summary([]) == []
    assert total_vat([]) == Decimal("0.00")
