"""Independent standard-library checks. Does not execute GDScript."""
import copy
import itertools
from pathlib import Path
import random
import sys
import unittest

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "tools"))
from campaign import DIRS, escapes, generate, solve, validate


class AuthoringTests(unittest.TestCase):
    def test_four_directions_at_edges(self):
        for name, (dx, dy) in DIRS.items():
            arrow = {"id": 0, "cells": [[1 + dx, 1 + dy]], "direction": name}
            self.assertTrue(escapes(arrow, [arrow], 3, 3))

    def test_distant_blocker_and_adjacent_lane(self):
        arrow = {"id": 0, "cells": [[0, 0], [1, 0]], "direction": "right"}
        blocker = {"id": 1, "cells": [[4, 0]], "direction": "up"}
        self.assertFalse(escapes(arrow, [arrow, blocker], 5, 3))
        blocker["cells"] = [[4, 1]]
        self.assertTrue(escapes(arrow, [arrow, blocker], 5, 3))

    def test_cycle(self):
        level = {"width": 3, "height": 2, "arrows": [
            {"id": 0, "cells": [[0, 0]], "direction": "right"},
            {"id": 1, "cells": [[2, 0]], "direction": "left"}]}
        self.assertIsNone(solve(level))

    def test_reject_invalid_data(self):
        level = {"width": 3, "height": 2, "arrows": [
            {"id": 0, "cells": [[0, 0]], "direction": "right"},
            {"id": 1, "cells": [[2, 0]], "direction": "up"}]}
        for field, value in [("cells", [[0, 0]]), ("cells", [[3, 0]]),
                             ("cells", [[1.5, 0]]), ("cells", [[1, 0], [2, 0]]),
                             ("cells", []), ("id", 0), ("direction", "diagonal")]:
            bad = copy.deepcopy(level)
            bad["arrows"][1][field] = value
            with self.subTest(field=field, value=value), self.assertRaises(AssertionError):
                validate(bad)

    def test_head_ray_matches_full_piece_sweep_exhaustively(self):
        # Independent comparison of the GDScript head-ray specification to a
        # physical swept-cell oracle, for all straight pieces on a 4x4 board.
        for direction, (dx, dy) in DIRS.items():
            for x, y, length in itertools.product(range(4), range(4), range(1, 4)):
                cells = [[x - i * dx, y - i * dy] for i in reversed(range(length))]
                if any(not (0 <= px < 4 and 0 <= py < 4) for px, py in cells):
                    continue
                arrow = {"id": 0, "cells": cells, "direction": direction}
                for bx, by in itertools.product(range(4), range(4)):
                    if [bx, by] in cells:
                        continue
                    blocker = {"id": 1, "cells": [[bx, by]], "direction": "up"}
                    ray = [(x + dx * n, y + dy * n) for n in range(1, 5)]
                    head_result = (bx, by) not in ray
                    self.assertEqual(head_result, escapes(arrow, [arrow, blocker], 4, 4))

    def test_generator_determinism_and_witness(self):
        for seed in range(40):
            level = generate(5, 5, 12, seed)
            self.assertIsNotNone(level)
            self.assertEqual(level, generate(5, 5, 12, seed))
            validate(level)
            self.assertIsNotNone(solve(level, random.Random(seed)))

    def test_every_legal_branch_of_small_generated_boards_finishes(self):
        def visit(arrows, w, h, seen):
            state = tuple(a["id"] for a in arrows)
            if not state or state in seen:
                return
            seen.add(state)
            legal = [a for a in arrows if escapes(a, arrows, w, h)]
            self.assertTrue(legal, f"Dead end in state {state}")
            for arrow in legal:
                visit([a for a in arrows if a != arrow], w, h, seen)
        for seed in range(20):
            level = generate(3, 3, 5, seed)
            self.assertIsNotNone(level)
            visit(level["arrows"], 3, 3, set())


if __name__ == "__main__":
    unittest.main()
