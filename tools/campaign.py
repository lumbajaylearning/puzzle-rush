#!/usr/bin/env python3
"""Standard-library authoring and independent campaign validation.

This is NOT a GDScript runtime test. A separate Godot suite tests the actual engine.
"""
from __future__ import annotations
import argparse
import json
import random
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DIRS = {"up": (0, -1), "right": (1, 0), "down": (0, 1), "left": (-1, 0)}


def validate(level):
    w, h = level["width"], level["height"]
    assert type(w) is int and type(h) is int and 2 <= w <= 12 and 2 <= h <= 12
    assert level["arrows"], "Empty campaign level"
    ids, occupied = set(), set()
    for a in level["arrows"]:
        assert type(a["id"]) is int and a["id"] >= 0 and a["id"] not in ids
        ids.add(a["id"])
        assert a["direction"] in DIRS and a["cells"]
        dx, dy = DIRS[a["direction"]]
        previous = None
        for point in a["cells"]:
            assert len(point) == 2 and all(type(v) is int for v in point)
            x, y = point
            assert 0 <= x < w and 0 <= y < h and (x, y) not in occupied
            if previous is not None:
                assert (x - previous[0], y - previous[1]) == (dx, dy)
            occupied.add((x, y))
            previous = point


def escapes(arrow, arrows, w, h):
    """Independent oracle: sweep EVERY piece cell to outside the board."""
    occupied = {tuple(p) for a in arrows if a["id"] != arrow["id"] for p in a["cells"]}
    dx, dy = DIRS[arrow["direction"]]
    for x, y in arrow["cells"]:
        for step in range(1, max(w, h) + 1):
            p = x + dx * step, y + dy * step
            if not (0 <= p[0] < w and 0 <= p[1] < h):
                break
            if p in occupied:
                return False
    return True


def solve(level, rng=None):
    remaining = list(level["arrows"])
    moves = []
    while remaining:
        legal = [a for a in remaining if escapes(a, remaining, level["width"], level["height"])]
        if not legal:
            return None
        selected = rng.choice(legal) if rng else min(legal, key=lambda a: a["id"])
        moves.append(selected["id"])
        remaining.remove(selected)
    return moves


def evaluate(level):
    remaining = list(level["arrows"])
    layers = choices = forced = 0
    while remaining:
        legal = [a for a in remaining if escapes(a, remaining, level["width"], level["height"])]
        if not legal:
            return {"solvable": False, "score": -1}
        if layers == 0:
            choices = len(legal)
        layers += 1
        forced += len(legal) == 1
        remaining = [a for a in remaining if a not in legal]
    return {"solvable": True, "arrows": len(level["arrows"]), "initial_choices": choices,
            "dependency_layers": layers, "forced_layers": forced,
            "score": len(level["arrows"]) + layers * 5 + forced * 2 - choices}


def generate(w, h, count, seed):
    rng = random.Random(seed)
    level = {"width": w, "height": h, "arrows": []}
    for _ in range(count * 250):
        if len(level["arrows"]) == count:
            break
        direction = rng.choice(list(DIRS))
        dx, dy = DIRS[direction]
        x, y = rng.randrange(w), rng.randrange(h)
        length = 2 if rng.random() < 0.35 else 1
        cells = [[x - dx * i, y - dy * i] for i in reversed(range(length))]
        occupied = {tuple(p) for a in level["arrows"] for p in a["cells"]}
        if any(not (0 <= p[0] < w and 0 <= p[1] < h) or tuple(p) in occupied for p in cells):
            continue
        arrow = {"id": len(level["arrows"]), "cells": cells, "direction": direction, "color": rng.randrange(4)}
        if escapes(arrow, level["arrows"] + [arrow], w, h):
            level["arrows"].append(arrow)
    if len(level["arrows"]) != count:
        return None
    level["seed"] = seed
    level["solution"] = list(reversed(range(count)))
    return level


def build():
    names = ["First light", "Open water", "Small ripples", "Find your flow", "Cross current", "Clear skies",
             "Soft turn", "Follow through", "Quiet momentum", "Drift away", "A little spark", "Live wire",
             "Chain reaction", "Bright idea", "Good vibrations", "Afterglow", "Neon crossing", "Light it up",
             "Electric avenue", "Spark flight", "Into the surge", "New frequency", "Full spectrum", "In the zone",
             "Fast lane", "Perfect rhythm", "Prism break", "Pulse runner", "Almost there", "Supernova"]
    for number in range(1, 31):
        path = ROOT / "levels" / f"level_{number:02}.json"
        if number == 1:
            level = json.loads(path.read_text())
            level["solution"] = solve(level)
        else:
            w = 4 if number <= 6 else 5 if number <= 15 else 6 if number <= 24 else 7
            h = w if number <= 15 else w + 1
            count = min(w * h - 3, 5 + number)
            candidates = [generate(w, h, count, number * 1000 + s) for s in range(24)]
            candidates = [c for c in candidates if c]
            assert candidates, number
            level = max(candidates, key=lambda c: evaluate(c)["score"])
        level.update(id=number, title=names[number - 1], chapter=["Drift", "Spark", "Surge"][(number - 1) // 10])
        level["hint"] = {1: "Tap the right arrow first. Clear a path for the others.",
                         2: "An arrow escapes only when its entire path is clear.",
                         3: "Keep clean moves under 2.4 seconds apart to build a combo.",
                         4: "Three stars: clear the board without a blocked tap.",
                         11: "New chapter. Longer paths, bigger chains.",
                         21: "Welcome to Surge. Read the board, then find your rhythm."}.get(number, "")
        level["difficulty"] = evaluate(level)
        validate(level)
        path.write_text(json.dumps(level, indent=2) + "\n")
    manifest = {"version": 1, "levels": [f"res://levels/level_{n:02}.json" for n in range(1, 31)]}
    (ROOT / "levels" / "campaign.json").write_text(json.dumps(manifest, indent=2) + "\n")


def verify():
    manifest = json.loads((ROOT / "levels" / "campaign.json").read_text())
    assert len(manifest["levels"]) == 30
    fingerprints = set()
    rows = []
    for number, resource in enumerate(manifest["levels"], 1):
        level = json.loads((ROOT / resource.removeprefix("res://")).read_text())
        validate(level)
        assert level["id"] == number
        fingerprint = json.dumps([{k: a[k] for k in ["cells", "direction"]} for a in level["arrows"]], sort_keys=True)
        assert fingerprint not in fingerprints
        fingerprints.add(fingerprint)
        assert solve(level) is not None
        remaining = list(level["arrows"])
        for arrow_id in level["solution"]:
            arrow = next(a for a in remaining if a["id"] == arrow_id)
            assert escapes(arrow, remaining, level["width"], level["height"])
            remaining.remove(arrow)
        assert not remaining
        for trial in range(100):
            assert solve(level, random.Random(number * 100 + trial)) is not None
        assert evaluate(level) == level["difficulty"]
        rows.append({"level": number, "size": f'{level["width"]}x{level["height"]}', **level["difficulty"]})
    print(json.dumps({"levels_verified": len(rows), "random_solution_runs": 3000, "levels": rows}, indent=2))


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("action", choices=["build", "verify"])
    args = parser.parse_args()
    build() if args.action == "build" else verify()
