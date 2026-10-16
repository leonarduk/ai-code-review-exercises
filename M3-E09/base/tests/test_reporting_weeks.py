from datetime import date

from reporting_weeks import week_label


def test_week_label():
    assert week_label(date(2026, 2, 11)) == "2026-06"
