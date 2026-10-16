# AI Code Review for Finance Developers: exercise pack

**Download:** [learner-pack.zip](https://github.com/leonarduk/ai-code-review-exercises/releases/latest/download/learner-pack.zip), or browse the folders below.

This is the exercise pack for the Udemy practice-test course *AI Code Review for Finance Developers: Practice Tests*. It holds no answers: those are in the course.

Each practice-test question is about one pull request. Its folder is named
after the question's exercise label, for example `M2-E05`, and contains:

| File | What it is |
|---|---|
| `PR.md` | The PR's title and description. Its requirements are the spec you review against. |
| `diff.patch` | The change as submitted, base to PR. This is what you review. |
| `base/` | The code before the PR. |
| `pr/` | The code after the PR, with the tests the PR arrived with. |

## How to use it

1. Read `PR.md`, then review `diff.patch`. Open files in `pr/` for context.
2. Answer the question for that exercise in the practice test.
3. Read the explanation Udemy shows after you answer. Every defect it names
   has been proven with a test that fails on this PR and passes on the fixed
   version, and every "not a defect" option has been proven too.

The PRs come from many codebases and were written the way AI assistants write
code. Some of them have no defects; approving a correct PR is a review skill
too.

## Running the PR's own tests (optional)

The PR's tests pass, as AI-written PRs usually do. Running them, and writing
your own to test a suspicion, is a good way to work through an exercise.

Python exercises (`M1`, `M3`, `M5`) need Python 3.12 or later:

    pip install pytest tzdata
    python run_pr_tests.py M1-E04

Java exercises (`M2`, `M4`, `M6`) need JDK 21 and the JUnit 5 console
launcher, `junit-platform-console-standalone-1.11.4.jar`, from Maven Central:

    https://repo.maven.apache.org/maven2/org/junit/platform/junit-platform-console-standalone/1.11.4/

    python run_pr_tests.py M2-E05 --junit junit-platform-console-standalone-1.11.4.jar

Add `--base` to run the code before the PR instead.

`tzdata` is only needed on Windows, which has no system time-zone database;
the date and time-zone modules use it.
