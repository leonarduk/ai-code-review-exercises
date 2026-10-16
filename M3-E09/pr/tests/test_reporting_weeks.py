from datetime import date, datetime, timezone

import pytest

from reporting_weeks import report_week, week_bounds, week_label, weeks_in_year


def test_week_label_mid_year():
    assert week_label(date(2026, 2, 11)) == "2026-W07"


def test_week_bounds_run_monday_to_sunday():
    assert week_bounds("2026-W07") == (date(2026, 2, 9), date(2026, 2, 15))


@pytest.mark.parametrize("label", ["2026-7", "2026-W00", "26-W07"])
def test_malformed_labels_are_rejected(label):
    with pytest.raises(ValueError):
        week_bounds(label)


def test_weeks_in_year():
    assert weeks_in_year(2025) == 52


def test_report_week_uses_the_london_trade_date():
    assert report_week(datetime(2026, 2, 11, 9, 0, tzinfo=timezone.utc)) == "2026-W07"


def test_naive_timestamps_are_rejected():
    with pytest.raises(ValueError):
        report_week(datetime(2026, 2, 11, 9, 0))
