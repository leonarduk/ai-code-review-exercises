# Schedule fix captures in market time

Fix-capture jobs run on a cron in UTC, so the 16:00 London capture runs at
17:00 London time all summer and stores the wrong fixing (MKT-877). This
adds a schedule expressed as a local time in the market's own zone.

## Requirements

- A schedule is a local time of day in a zone, e.g. 16:00 Europe/London.
  Each run is at that local time as observed on the day, summer or winter.
- `nextDailyRun(after)`: the first run strictly after `after`.
- `nextMonthlyRun(after)`: the first run on the 1st of a month strictly
  after `after`.
- If the local time does not exist on a day because the clocks go forward,
  that day's run is moved later by the length of the gap (01:30 becomes
  02:30). If it occurs twice because the clocks go back, the run is at the
  first occurrence.

## Changes

- New `RunSchedule` with `nextDailyRun` and `nextMonthlyRun`.
- Tests for runs later the same day, the next day, in summer time, and a
  monthly run.
