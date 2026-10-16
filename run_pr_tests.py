"""Run one exercise's PR tests: the tests the PR arrived with.

    python run_pr_tests.py M1-E04              # a Python exercise
    python run_pr_tests.py M2-E05 --junit junit-platform-console-standalone-1.11.4.jar
    python run_pr_tests.py M2-E05 --base       # the code before the PR instead

Python exercises need pytest (pip install pytest tzdata). Java exercises need
JDK 21 and the JUnit 5 console launcher jar (see README.md). Only the
standard library is used here.
"""

import argparse
import os
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

HERE = Path(__file__).resolve().parent


def run_python(tree):
    tests = tree / "tests"
    if not tests.exists():
        sys.exit(f"{tree} has no tests/ directory")
    env = dict(os.environ, PYTHONPATH=str(tree), PYTHONDONTWRITEBYTECODE="1")
    return subprocess.call(
        [sys.executable, "-m", "pytest", "-q", "-p", "no:cacheprovider",
         "--import-mode=importlib", str(tests)],
        cwd=tree, env=env)


def javac(sources, out, classpath):
    cmd = ["javac", "--release", "21", "-encoding", "UTF-8", "-d", str(out)]
    if classpath:
        cmd += ["-cp", os.pathsep.join(map(str, classpath))]
    if subprocess.call(cmd + [str(s) for s in sources]):
        sys.exit("compilation failed")


def run_java(tree, junit):
    if not junit or not Path(junit).is_file():
        sys.exit("Java exercises need --junit <path to junit-platform-console-standalone-1.11.4.jar>")
    with tempfile.TemporaryDirectory() as tmp:
        main, test = Path(tmp) / "main", Path(tmp) / "test"
        javac(sorted((tree / "src").rglob("*.java")), main, [])
        javac(sorted((tree / "tests").rglob("*.java")), test, [main, junit])
        classpath = [test, main] + ([tree / "resources"] if (tree / "resources").exists() else [])
        return subprocess.call(
            ["java", "-jar", str(junit), "execute", "--disable-banner",
             "--class-path", os.pathsep.join(map(str, classpath)),
             "--scan-class-path", str(test)])


def main():
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("exercise", help="exercise folder, e.g. M1-E04")
    parser.add_argument("--junit", help="path to the JUnit console launcher jar (Java only)")
    parser.add_argument("--base", action="store_true", help="run base/ instead of pr/")
    args = parser.parse_args()
    folder = HERE / args.exercise.upper()
    tree = folder / ("base" if args.base else "pr")
    if not tree.is_dir():
        sys.exit(f"no such exercise folder: {tree}")
    if shutil.which("java") is None and (tree / "src").exists():
        sys.exit("this is a Java exercise; install JDK 21 first")
    code = run_java(tree, args.junit) if (tree / "src").exists() else run_python(tree)
    sys.exit(code)


if __name__ == "__main__":
    main()
