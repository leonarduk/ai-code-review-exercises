from datetime import date

import pytest

from business_days import adjust_schedule, following, modified_following, preceding

NO_HOLIDAYS: set[date] = set()


def test_following_rolls_a_saturday_to_monday():
    assert following(date(2026, 10, 17), NO_HOLIDAYS) == date(2026, 10, 19)


def test_preceding_rolls_a_saturday_to_friday():
    assert preceding(date(2026, 10, 17), NO_HOLIDAYS) == date(2026, 10, 16)


@pytest.mark.parametrize(
    ("unadjusted", "expected"),
    [
        (date(2026, 10, 17), date(2026, 10, 19)),  # stays in October: roll forward
        (date(2026, 10, 31), date(2026, 10, 30)),  # would cross into November: roll back
        (date(2026, 5, 31), date(2026, 5, 29)),    # would cross into June: roll back
        (date(2026, 10, 20), date(2026, 10, 20)),  # already a business day
    ],
)
def test_modified_following(unadjusted, expected):
    assert modified_following(unadjusted, NO_HOLIDAYS) == expected


def test_adjust_schedule_uses_the_named_convention():
    schedule = [date(2026, 10, 31), date(2026, 11, 30)]
    assert adjust_schedule(schedule, NO_HOLIDAYS, "following") == [
        date(2026, 11, 2), date(2026, 11, 30),
    ]


def test_unknown_convention_is_rejected():
    with pytest.raises(ValueError):
        adjust_schedule([date(2026, 10, 31)], NO_HOLIDAYS, "nearest")
