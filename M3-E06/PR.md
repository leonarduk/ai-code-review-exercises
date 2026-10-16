# Merge venue execution reports into one timeline

Best-execution review needs every execution across venues on one timeline
(EXEC-77). Venues report timestamps differently; this normalises and merges
them.

## Requirements

- ISO 8601 venues send timestamps with `Z` or a UTC offset
  (`2026-03-10T09:30:00-05:00`); a timestamp without one is rejected with
  `TimestampError`.
- The legacy venue sends London wall-clock time without an offset
  (`2026-03-10 11:00:00`). A wall-clock time that does not exist because the
  clocks went forward is rejected with `TimestampError`. A time that occurs
  twice when the clocks go back is taken as its first occurrence.
- `merge` returns every execution ordered by the instant it happened,
  earliest first; executions at the same instant keep their input order.
- Each `Execution` keeps the venue's original timestamp text for audit.

## Changes

- New `executions` module: `parse_iso`, `parse_legacy`, `execution` and
  `merge`.
- Tests for both formats, summer and winter, and merging.
