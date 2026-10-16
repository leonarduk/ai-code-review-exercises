# Report transactions by ISO 8601 week

The regulator's weekly transaction report is keyed by ISO 8601 week, but
`week_label` uses Sunday-start weeks numbered within the calendar year, so
our weeks disagree with theirs around every new year (REG-415).

## Requirements

- `week_label(day)` returns the ISO 8601 week of a trade date as
  `YYYY-Www`, for example `2026-W07`. ISO weeks start on Monday, week 1 is
  the week that contains the year's first Thursday, and `YYYY` is the ISO
  week-numbering year, which can differ from the calendar year for the
  first and last few days of a year.
- `weeks_in_year(year)` returns the number of ISO weeks in a year, 52 or 53.
- `week_bounds(label)` returns the Monday and the Sunday of the week a label
  names. A malformed label, or one naming a week that its year does not
  have, is rejected with `ValueError`.
- `report_week(executed_at)` returns the label for the London trade date of
  an execution. Timestamps must be timezone-aware and may be in any zone;
  naive timestamps are rejected with `ValueError`.

## Changes

- `week_label` switches to ISO weeks; new `weeks_in_year`, `week_bounds` and
  `report_week`.
- Tests for labels, bounds, label validation and the London trade date.
