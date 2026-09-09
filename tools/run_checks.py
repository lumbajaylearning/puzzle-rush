#!/usr/bin/env python3
"""Run real Godot suites with isolated saves; fail on script errors even with exit 0."""
import argparse
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys
import tempfile

ROOT = Path(__file__).resolve().parents[1]


def run(command, env):
    result = subprocess.run(command, cwd=ROOT, env=env, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, timeout=180)
    print(result.stdout)
    if result.returncode or re.search(r"SCRIPT ERROR:|Parse Error:|^ERROR:", result.stdout, re.M):
        raise SystemExit(result.returncode or 1)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--godot", default=os.environ.get("GODOT_BIN", "godot"))
    args = parser.parse_args()
    binary = shutil.which(args.godot)
    if not binary:
        raise SystemExit("Godot not found. Install Godot 4.5.2 and pass --godot /path/to/binary. No runtime tests were run.")
    run([sys.executable, "-m", "unittest", "discover", "-s", "tests", "-p", "test_*.py", "-v"], os.environ.copy())
    run([sys.executable, "tools/campaign.py", "verify"], os.environ.copy())
    # Import/class registration before --script execution. Keep normal editor configuration.
    run([binary, "--headless", "--editor", "--path", str(ROOT), "--import"], os.environ.copy())
    for suite in ["engine", "campaign", "session", "app"]:
        with tempfile.TemporaryDirectory(prefix="arrow-rush-test-") as directory:
            env = os.environ.copy()
            # Godot's user:// resolves under these standard per-user application dirs.
            # Tests never change HOME or touch the player's normal save.
            env["XDG_DATA_HOME"] = directory
            env["APPDATA"] = directory
            if sys.platform == "darwin":
                raise SystemExit("Use Linux/Windows for isolated save tests; macOS uses a different app-data path.")
            run([binary, "--headless", "--audio-driver", "Dummy", "--path", str(ROOT), "--script", f"tests/test_{suite}.gd"], env)
    print("PASS: all real Godot suites")


if __name__ == "__main__":
    main()
