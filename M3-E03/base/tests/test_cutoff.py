from datetime import date, datetime

from cutoff import booking_date


def test_after_cut_off_books_next_day():
    assert booking_date(datetime(2026, 1, 15, 22, 30)) == date(2026, 1, 16)
