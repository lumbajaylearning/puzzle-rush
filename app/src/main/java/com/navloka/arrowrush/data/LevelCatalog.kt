package com.navloka.arrowrush.data

import com.navloka.arrowrush.core.model.Arrow
import com.navloka.arrowrush.core.model.Difficulty
import com.navloka.arrowrush.core.model.Direction
import com.navloka.arrowrush.core.model.PuzzleLevel

object LevelCatalog {

    val levels: List<PuzzleLevel> by lazy {
        listOf(
            // Level 1: First Flight (3x3, 3 arrows)
            PuzzleLevel(
                levelNumber = 1,
                title = "First Flight",
                rows = 3,
                cols = 3,
                arrows = listOf(
                    Arrow(1, 1, 2, Direction.RIGHT, 0),
                    Arrow(2, 1, 1, Direction.RIGHT, 1),
                    Arrow(3, 1, 0, Direction.RIGHT, 2)
                ),
                difficulty = Difficulty.EASY
            ),
            // Level 2: Outward Bound (3x3, 4 arrows)
            PuzzleLevel(
                levelNumber = 2,
                title = "Outward Bound",
                rows = 3,
                cols = 3,
                arrows = listOf(
                    Arrow(1, 0, 1, Direction.UP, 0),
                    Arrow(2, 1, 2, Direction.RIGHT, 1),
                    Arrow(3, 2, 1, Direction.DOWN, 2),
                    Arrow(4, 1, 0, Direction.LEFT, 3)
                ),
                difficulty = Difficulty.EASY
            ),
            // Level 3: Crossroad Peek (3x3, 4 arrows)
            PuzzleLevel(
                levelNumber = 3,
                title = "Crossroad Peek",
                rows = 3,
                cols = 3,
                arrows = listOf(
                    Arrow(1, 0, 2, Direction.UP, 0),
                    Arrow(2, 1, 2, Direction.UP, 1),
                    Arrow(3, 1, 1, Direction.RIGHT, 2),
                    Arrow(4, 1, 0, Direction.RIGHT, 3)
                ),
                difficulty = Difficulty.EASY
            ),
            // Level 4: The Pinwheel (3x3, 4 arrows)
            PuzzleLevel(
                levelNumber = 4,
                title = "The Pinwheel",
                rows = 3,
                cols = 3,
                arrows = listOf(
                    Arrow(1, 0, 0, Direction.RIGHT, 0),
                    Arrow(2, 0, 2, Direction.UP, 1),
                    Arrow(3, 2, 2, Direction.LEFT, 2),
                    Arrow(4, 2, 0, Direction.UP, 3)
                ),
                difficulty = Difficulty.EASY
            ),
            // Level 5: Corner Lock (3x3, 5 arrows)
            PuzzleLevel(
                levelNumber = 5,
                title = "Corner Lock",
                rows = 3,
                cols = 3,
                arrows = listOf(
                    Arrow(1, 0, 2, Direction.RIGHT, 0),
                    Arrow(2, 0, 1, Direction.RIGHT, 1),
                    Arrow(3, 1, 1, Direction.UP, 2),
                    Arrow(4, 1, 0, Direction.RIGHT, 3),
                    Arrow(5, 2, 0, Direction.UP, 0)
                ),
                difficulty = Difficulty.EASY
            ),
            // Level 6: Traffic Knot (4x4, 6 arrows)
            PuzzleLevel(
                levelNumber = 6,
                title = "Traffic Knot",
                rows = 4,
                cols = 4,
                arrows = listOf(
                    Arrow(1, 0, 1, Direction.UP, 0),
                    Arrow(2, 1, 1, Direction.UP, 1),
                    Arrow(3, 2, 1, Direction.RIGHT, 2),
                    Arrow(4, 2, 2, Direction.RIGHT, 3),
                    Arrow(5, 3, 2, Direction.UP, 0),
                    Arrow(6, 1, 2, Direction.RIGHT, 1)
                ),
                difficulty = Difficulty.EASY
            ),
            // Level 7: Outer Peeling (4x4, 8 arrows)
            PuzzleLevel(
                levelNumber = 7,
                title = "Outer Peeling",
                rows = 4,
                cols = 4,
                arrows = listOf(
                    Arrow(1, 0, 3, Direction.RIGHT, 0),
                    Arrow(2, 0, 2, Direction.RIGHT, 1),
                    Arrow(3, 1, 2, Direction.UP, 2),
                    Arrow(4, 1, 1, Direction.RIGHT, 3),
                    Arrow(5, 2, 1, Direction.UP, 0),
                    Arrow(6, 2, 0, Direction.RIGHT, 1),
                    Arrow(7, 3, 0, Direction.UP, 2),
                    Arrow(8, 3, 3, Direction.DOWN, 3)
                ),
                difficulty = Difficulty.EASY
            ),
            // Level 8: The Zipper (4x4, 8 arrows)
            PuzzleLevel(
                levelNumber = 8,
                title = "The Zipper",
                rows = 4,
                cols = 4,
                arrows = listOf(
                    Arrow(1, 0, 0, Direction.LEFT, 0),
                    Arrow(2, 0, 1, Direction.LEFT, 1),
                    Arrow(3, 1, 1, Direction.UP, 2),
                    Arrow(4, 1, 2, Direction.LEFT, 3),
                    Arrow(5, 2, 2, Direction.UP, 0),
                    Arrow(6, 2, 3, Direction.LEFT, 1),
                    Arrow(7, 3, 3, Direction.UP, 2),
                    Arrow(8, 3, 1, Direction.DOWN, 3)
                ),
                difficulty = Difficulty.EASY
            ),
            // Level 9: Crossroads (4x4, 9 arrows)
            PuzzleLevel(
                levelNumber = 9,
                title = "Crossroads",
                rows = 4,
                cols = 4,
                arrows = listOf(
                    Arrow(1, 1, 3, Direction.RIGHT, 0),
                    Arrow(2, 1, 2, Direction.RIGHT, 1),
                    Arrow(3, 1, 1, Direction.RIGHT, 2),
                    Arrow(4, 0, 1, Direction.UP, 3),
                    Arrow(5, 2, 1, Direction.DOWN, 0),
                    Arrow(6, 3, 1, Direction.DOWN, 1),
                    Arrow(7, 2, 2, Direction.DOWN, 2),
                    Arrow(8, 3, 2, Direction.DOWN, 3),
                    Arrow(9, 2, 0, Direction.LEFT, 0)
                ),
                difficulty = Difficulty.EASY
            ),
            // Level 10: Diamond Core (4x4, 10 arrows)
            PuzzleLevel(
                levelNumber = 10,
                title = "Diamond Core",
                rows = 4,
                cols = 4,
                arrows = listOf(
                    Arrow(1, 0, 1, Direction.UP, 0),
                    Arrow(2, 0, 2, Direction.UP, 1),
                    Arrow(3, 1, 3, Direction.RIGHT, 2),
                    Arrow(4, 2, 3, Direction.RIGHT, 3),
                    Arrow(5, 3, 2, Direction.DOWN, 0),
                    Arrow(6, 3, 1, Direction.DOWN, 1),
                    Arrow(7, 2, 0, Direction.LEFT, 2),
                    Arrow(8, 1, 0, Direction.LEFT, 3),
                    Arrow(9, 1, 1, Direction.UP, 0),
                    Arrow(10, 2, 2, Direction.DOWN, 1)
                ),
                difficulty = Difficulty.EASY
            ),
            // Level 11: Boxed In (5x5, 11 arrows)
            PuzzleLevel(
                levelNumber = 11,
                title = "Boxed In",
                rows = 5,
                cols = 5,
                arrows = listOf(
                    Arrow(1, 0, 2, Direction.UP, 0),
                    Arrow(2, 1, 2, Direction.UP, 1),
                    Arrow(3, 2, 2, Direction.UP, 2),
                    Arrow(4, 2, 1, Direction.LEFT, 3),
                    Arrow(5, 2, 0, Direction.LEFT, 0),
                    Arrow(6, 2, 3, Direction.RIGHT, 1),
                    Arrow(7, 2, 4, Direction.RIGHT, 2),
                    Arrow(8, 3, 2, Direction.DOWN, 3),
                    Arrow(9, 4, 2, Direction.DOWN, 0),
                    Arrow(10, 1, 1, Direction.LEFT, 1),
                    Arrow(11, 3, 3, Direction.RIGHT, 2)
                ),
                difficulty = Difficulty.MEDIUM
            ),
            // Level 12: Spiral Step (5x5, 12 arrows)
            PuzzleLevel(
                levelNumber = 12,
                title = "Spiral Step",
                rows = 5,
                cols = 5,
                arrows = listOf(
                    Arrow(1, 0, 4, Direction.RIGHT, 0),
                    Arrow(2, 0, 3, Direction.RIGHT, 1),
                    Arrow(3, 0, 2, Direction.RIGHT, 2),
                    Arrow(4, 0, 1, Direction.RIGHT, 3),
                    Arrow(5, 1, 1, Direction.UP, 0),
                    Arrow(6, 2, 1, Direction.UP, 1),
                    Arrow(7, 3, 1, Direction.UP, 2),
                    Arrow(8, 3, 2, Direction.LEFT, 3),
                    Arrow(9, 3, 3, Direction.LEFT, 0),
                    Arrow(10, 2, 3, Direction.DOWN, 1),
                    Arrow(11, 1, 3, Direction.DOWN, 2),
                    Arrow(12, 1, 2, Direction.RIGHT, 3)
                ),
                difficulty = Difficulty.MEDIUM
            ),
            // Level 13: Twin Corridors (5x5, 13 arrows)
            PuzzleLevel(
                levelNumber = 13,
                title = "Twin Corridors",
                rows = 5,
                cols = 5,
                arrows = listOf(
                    Arrow(1, 0, 0, Direction.UP, 0),
                    Arrow(2, 1, 0, Direction.UP, 1),
                    Arrow(3, 2, 0, Direction.UP, 2),
                    Arrow(4, 0, 4, Direction.UP, 3),
                    Arrow(5, 1, 4, Direction.UP, 0),
                    Arrow(6, 2, 4, Direction.UP, 1),
                    Arrow(7, 2, 1, Direction.LEFT, 2),
                    Arrow(8, 2, 2, Direction.LEFT, 3),
                    Arrow(9, 2, 3, Direction.RIGHT, 0),
                    Arrow(10, 3, 2, Direction.DOWN, 1),
                    Arrow(11, 4, 2, Direction.DOWN, 2),
                    Arrow(12, 4, 1, Direction.DOWN, 3),
                    Arrow(13, 4, 3, Direction.DOWN, 0)
                ),
                difficulty = Difficulty.MEDIUM
            ),
            // Level 14: The Ring (5x5, 14 arrows)
            PuzzleLevel(
                levelNumber = 14,
                title = "The Ring",
                rows = 5,
                cols = 5,
                arrows = listOf(
                    Arrow(1, 0, 1, Direction.UP, 0),
                    Arrow(2, 0, 2, Direction.UP, 1),
                    Arrow(3, 0, 3, Direction.UP, 2),
                    Arrow(4, 1, 4, Direction.RIGHT, 3),
                    Arrow(5, 2, 4, Direction.RIGHT, 0),
                    Arrow(6, 3, 4, Direction.RIGHT, 1),
                    Arrow(7, 4, 3, Direction.DOWN, 2),
                    Arrow(8, 4, 2, Direction.DOWN, 3),
                    Arrow(9, 4, 1, Direction.DOWN, 0),
                    Arrow(10, 3, 0, Direction.LEFT, 1),
                    Arrow(11, 2, 0, Direction.LEFT, 2),
                    Arrow(12, 1, 0, Direction.LEFT, 3),
                    Arrow(13, 1, 2, Direction.UP, 0),
                    Arrow(14, 3, 2, Direction.DOWN, 1)
                ),
                difficulty = Difficulty.MEDIUM
            ),
            // Level 15: Vortex (5x5, 15 arrows)
            PuzzleLevel(
                levelNumber = 15,
                title = "Vortex",
                rows = 5,
                cols = 5,
                arrows = listOf(
                    Arrow(1, 0, 0, Direction.LEFT, 0),
                    Arrow(2, 0, 1, Direction.UP, 1),
                    Arrow(3, 0, 2, Direction.UP, 2),
                    Arrow(4, 0, 3, Direction.UP, 3),
                    Arrow(5, 0, 4, Direction.RIGHT, 0),
                    Arrow(6, 1, 4, Direction.RIGHT, 1),
                    Arrow(7, 2, 4, Direction.RIGHT, 2),
                    Arrow(8, 3, 4, Direction.RIGHT, 3),
                    Arrow(9, 4, 4, Direction.DOWN, 0),
                    Arrow(10, 4, 3, Direction.DOWN, 1),
                    Arrow(11, 4, 2, Direction.DOWN, 2),
                    Arrow(12, 4, 1, Direction.DOWN, 3),
                    Arrow(13, 4, 0, Direction.LEFT, 0),
                    Arrow(14, 3, 0, Direction.LEFT, 1),
                    Arrow(15, 2, 0, Direction.LEFT, 2)
                ),
                difficulty = Difficulty.MEDIUM
            ),
            // Level 16: Interlocking Teeth (5x5, 16 arrows)
            PuzzleLevel(
                levelNumber = 16,
                title = "Interlocking Teeth",
                rows = 5,
                cols = 5,
                arrows = listOf(
                    Arrow(1, 0, 1, Direction.UP, 0),
                    Arrow(2, 0, 3, Direction.UP, 1),
                    Arrow(3, 1, 1, Direction.UP, 2),
                    Arrow(4, 1, 3, Direction.UP, 3),
                    Arrow(5, 3, 1, Direction.DOWN, 0),
                    Arrow(6, 3, 3, Direction.DOWN, 1),
                    Arrow(7, 4, 1, Direction.DOWN, 2),
                    Arrow(8, 4, 3, Direction.DOWN, 3),
                    Arrow(9, 2, 0, Direction.LEFT, 0),
                    Arrow(10, 2, 1, Direction.LEFT, 1),
                    Arrow(11, 2, 2, Direction.LEFT, 2),
                    Arrow(12, 2, 3, Direction.RIGHT, 3),
                    Arrow(13, 2, 4, Direction.RIGHT, 0),
                    Arrow(14, 1, 2, Direction.UP, 1),
                    Arrow(15, 3, 2, Direction.DOWN, 2),
                    Arrow(16, 0, 2, Direction.UP, 3)
                ),
                difficulty = Difficulty.MEDIUM
            ),
            // Level 17: The Cascade (5x5, 16 arrows)
            PuzzleLevel(
                levelNumber = 17,
                title = "The Cascade",
                rows = 5,
                cols = 5,
                arrows = listOf(
                    Arrow(1, 0, 4, Direction.RIGHT, 0),
                    Arrow(2, 1, 4, Direction.RIGHT, 1),
                    Arrow(3, 2, 4, Direction.RIGHT, 2),
                    Arrow(4, 3, 4, Direction.RIGHT, 3),
                    Arrow(5, 0, 3, Direction.RIGHT, 0),
                    Arrow(6, 1, 3, Direction.RIGHT, 1),
                    Arrow(7, 2, 3, Direction.RIGHT, 2),
                    Arrow(8, 3, 3, Direction.RIGHT, 3),
                    Arrow(9, 0, 2, Direction.RIGHT, 0),
                    Arrow(10, 1, 2, Direction.RIGHT, 1),
                    Arrow(11, 2, 2, Direction.RIGHT, 2),
                    Arrow(12, 3, 2, Direction.RIGHT, 3),
                    Arrow(13, 4, 0, Direction.DOWN, 0),
                    Arrow(14, 4, 1, Direction.DOWN, 1),
                    Arrow(15, 4, 2, Direction.DOWN, 2),
                    Arrow(16, 4, 3, Direction.DOWN, 3)
                ),
                difficulty = Difficulty.MEDIUM
            ),
            // Level 18: Symmetry Trap (5x5, 17 arrows)
            PuzzleLevel(
                levelNumber = 18,
                title = "Symmetry Trap",
                rows = 5,
                cols = 5,
                arrows = listOf(
                    Arrow(1, 0, 2, Direction.UP, 0),
                    Arrow(2, 1, 2, Direction.UP, 1),
                    Arrow(3, 3, 2, Direction.DOWN, 2),
                    Arrow(4, 4, 2, Direction.DOWN, 3),
                    Arrow(5, 2, 0, Direction.LEFT, 0),
                    Arrow(6, 2, 1, Direction.LEFT, 1),
                    Arrow(7, 2, 3, Direction.RIGHT, 2),
                    Arrow(8, 2, 4, Direction.RIGHT, 3),
                    Arrow(9, 0, 0, Direction.LEFT, 0),
                    Arrow(10, 0, 4, Direction.RIGHT, 1),
                    Arrow(11, 4, 0, Direction.LEFT, 2),
                    Arrow(12, 4, 4, Direction.RIGHT, 3),
                    Arrow(13, 1, 1, Direction.UP, 0),
                    Arrow(14, 1, 3, Direction.UP, 1),
                    Arrow(15, 3, 1, Direction.DOWN, 2),
                    Arrow(16, 3, 3, Direction.DOWN, 3),
                    Arrow(17, 2, 2, Direction.UP, 0)
                ),
                difficulty = Difficulty.MEDIUM
            ),
            // Level 19: Hourglass Matrix (5x5, 18 arrows)
            PuzzleLevel(
                levelNumber = 19,
                title = "Hourglass Matrix",
                rows = 5,
                cols = 5,
                arrows = listOf(
                    Arrow(1, 0, 0, Direction.UP, 0),
                    Arrow(2, 0, 1, Direction.UP, 1),
                    Arrow(3, 0, 2, Direction.UP, 2),
                    Arrow(4, 0, 3, Direction.UP, 3),
                    Arrow(5, 0, 4, Direction.UP, 0),
                    Arrow(6, 1, 1, Direction.UP, 1),
                    Arrow(7, 1, 2, Direction.UP, 2),
                    Arrow(8, 1, 3, Direction.UP, 3),
                    Arrow(9, 2, 2, Direction.LEFT, 0),
                    Arrow(10, 2, 1, Direction.LEFT, 1),
                    Arrow(11, 2, 0, Direction.LEFT, 2),
                    Arrow(12, 3, 1, Direction.DOWN, 3),
                    Arrow(13, 3, 2, Direction.DOWN, 0),
                    Arrow(14, 3, 3, Direction.DOWN, 1),
                    Arrow(15, 4, 0, Direction.DOWN, 2),
                    Arrow(16, 4, 1, Direction.DOWN, 3),
                    Arrow(17, 4, 3, Direction.DOWN, 0),
                    Arrow(18, 4, 4, Direction.DOWN, 1)
                ),
                difficulty = Difficulty.MEDIUM
            ),
            // Level 20: Crossroads Central (5x5, 18 arrows)
            PuzzleLevel(
                levelNumber = 20,
                title = "Crossroads Central",
                rows = 5,
                cols = 5,
                arrows = listOf(
                    Arrow(1, 0, 2, Direction.UP, 0),
                    Arrow(2, 1, 2, Direction.UP, 1),
                    Arrow(3, 2, 2, Direction.RIGHT, 2),
                    Arrow(4, 2, 3, Direction.RIGHT, 3),
                    Arrow(5, 2, 4, Direction.RIGHT, 0),
                    Arrow(6, 4, 2, Direction.DOWN, 1),
                    Arrow(7, 3, 2, Direction.DOWN, 2),
                    Arrow(8, 2, 1, Direction.LEFT, 3),
                    Arrow(9, 2, 0, Direction.LEFT, 0),
                    Arrow(10, 0, 0, Direction.LEFT, 1),
                    Arrow(11, 0, 4, Direction.RIGHT, 2),
                    Arrow(12, 4, 0, Direction.LEFT, 3),
                    Arrow(13, 4, 4, Direction.RIGHT, 0),
                    Arrow(14, 1, 1, Direction.UP, 1),
                    Arrow(15, 1, 3, Direction.UP, 2),
                    Arrow(16, 3, 1, Direction.DOWN, 3),
                    Arrow(17, 3, 3, Direction.DOWN, 0),
                    Arrow(18, 1, 4, Direction.RIGHT, 1)
                ),
                difficulty = Difficulty.MEDIUM
            ),
            // Level 21: The Labyrinth (6x6, 19 arrows)
            PuzzleLevel(
                levelNumber = 21,
                title = "The Labyrinth",
                rows = 6,
                cols = 6,
                arrows = listOf(
                    Arrow(1, 0, 0, Direction.LEFT, 0),
                    Arrow(2, 0, 1, Direction.UP, 1),
                    Arrow(3, 0, 2, Direction.UP, 2),
                    Arrow(4, 0, 3, Direction.UP, 3),
                    Arrow(5, 0, 4, Direction.UP, 0),
                    Arrow(6, 0, 5, Direction.RIGHT, 1),
                    Arrow(7, 1, 5, Direction.RIGHT, 2),
                    Arrow(8, 2, 5, Direction.RIGHT, 3),
                    Arrow(9, 3, 5, Direction.RIGHT, 0),
                    Arrow(10, 4, 5, Direction.RIGHT, 1),
                    Arrow(11, 5, 5, Direction.DOWN, 2),
                    Arrow(12, 5, 4, Direction.DOWN, 3),
                    Arrow(13, 5, 3, Direction.DOWN, 0),
                    Arrow(14, 5, 2, Direction.DOWN, 1),
                    Arrow(15, 5, 1, Direction.DOWN, 2),
                    Arrow(16, 5, 0, Direction.LEFT, 3),
                    Arrow(17, 2, 2, Direction.UP, 0),
                    Arrow(18, 3, 3, Direction.DOWN, 1),
                    Arrow(19, 2, 3, Direction.RIGHT, 2)
                ),
                difficulty = Difficulty.HARD
            ),
            // Level 22: Twin Spirals (6x6, 20 arrows)
            PuzzleLevel(
                levelNumber = 22,
                title = "Twin Spirals",
                rows = 6,
                cols = 6,
                arrows = listOf(
                    Arrow(1, 0, 0, Direction.UP, 0),
                    Arrow(2, 1, 0, Direction.UP, 1),
                    Arrow(3, 2, 0, Direction.UP, 2),
                    Arrow(4, 3, 0, Direction.DOWN, 3),
                    Arrow(5, 4, 0, Direction.DOWN, 0),
                    Arrow(6, 5, 0, Direction.DOWN, 1),
                    Arrow(7, 0, 5, Direction.UP, 2),
                    Arrow(8, 1, 5, Direction.UP, 3),
                    Arrow(9, 2, 5, Direction.UP, 0),
                    Arrow(10, 3, 5, Direction.DOWN, 1),
                    Arrow(11, 4, 5, Direction.DOWN, 2),
                    Arrow(12, 5, 5, Direction.DOWN, 3),
                    Arrow(13, 1, 2, Direction.LEFT, 0),
                    Arrow(14, 1, 3, Direction.RIGHT, 1),
                    Arrow(15, 2, 2, Direction.LEFT, 2),
                    Arrow(16, 2, 3, Direction.RIGHT, 3),
                    Arrow(17, 3, 2, Direction.LEFT, 0),
                    Arrow(18, 3, 3, Direction.RIGHT, 1),
                    Arrow(19, 4, 2, Direction.LEFT, 2),
                    Arrow(20, 4, 3, Direction.RIGHT, 3)
                ),
                difficulty = Difficulty.HARD
            ),
            // Level 23: Checker Rush (6x6, 21 arrows)
            PuzzleLevel(
                levelNumber = 23,
                title = "Checker Rush",
                rows = 6,
                cols = 6,
                arrows = listOf(
                    Arrow(1, 0, 1, Direction.UP, 0),
                    Arrow(2, 0, 3, Direction.UP, 1),
                    Arrow(3, 0, 5, Direction.UP, 2),
                    Arrow(4, 1, 0, Direction.LEFT, 3),
                    Arrow(5, 1, 2, Direction.UP, 0),
                    Arrow(6, 1, 4, Direction.UP, 1),
                    Arrow(7, 2, 1, Direction.LEFT, 2),
                    Arrow(8, 2, 3, Direction.RIGHT, 3),
                    Arrow(9, 2, 5, Direction.RIGHT, 0),
                    Arrow(10, 3, 0, Direction.LEFT, 1),
                    Arrow(11, 3, 2, Direction.DOWN, 2),
                    Arrow(12, 3, 4, Direction.DOWN, 3),
                    Arrow(13, 4, 1, Direction.DOWN, 0),
                    Arrow(14, 4, 3, Direction.DOWN, 1),
                    Arrow(15, 4, 5, Direction.RIGHT, 2),
                    Arrow(16, 5, 0, Direction.DOWN, 3),
                    Arrow(17, 5, 2, Direction.DOWN, 0),
                    Arrow(18, 5, 4, Direction.DOWN, 1),
                    Arrow(19, 1, 1, Direction.LEFT, 2),
                    Arrow(20, 3, 3, Direction.RIGHT, 3),
                    Arrow(21, 2, 2, Direction.UP, 0)
                ),
                difficulty = Difficulty.HARD
            ),
            // Level 24: Whirlpool (6x6, 22 arrows)
            PuzzleLevel(
                levelNumber = 24,
                title = "Whirlpool",
                rows = 6,
                cols = 6,
                arrows = listOf(
                    Arrow(1, 0, 1, Direction.UP, 0),
                    Arrow(2, 0, 2, Direction.UP, 1),
                    Arrow(3, 0, 3, Direction.UP, 2),
                    Arrow(4, 0, 4, Direction.UP, 3),
                    Arrow(5, 1, 5, Direction.RIGHT, 0),
                    Arrow(6, 2, 5, Direction.RIGHT, 1),
                    Arrow(7, 3, 5, Direction.RIGHT, 2),
                    Arrow(8, 4, 5, Direction.RIGHT, 3),
                    Arrow(9, 5, 4, Direction.DOWN, 0),
                    Arrow(10, 5, 3, Direction.DOWN, 1),
                    Arrow(11, 5, 2, Direction.DOWN, 2),
                    Arrow(12, 5, 1, Direction.DOWN, 3),
                    Arrow(13, 4, 0, Direction.LEFT, 0),
                    Arrow(14, 3, 0, Direction.LEFT, 1),
                    Arrow(15, 2, 0, Direction.LEFT, 2),
                    Arrow(16, 1, 0, Direction.LEFT, 3),
                    Arrow(17, 1, 2, Direction.UP, 0),
                    Arrow(18, 1, 3, Direction.RIGHT, 1),
                    Arrow(19, 2, 4, Direction.DOWN, 2),
                    Arrow(20, 3, 4, Direction.DOWN, 3),
                    Arrow(21, 4, 3, Direction.LEFT, 0),
                    Arrow(22, 3, 2, Direction.UP, 1)
                ),
                difficulty = Difficulty.HARD
            ),
            // Level 25: Fortress Walls (6x6, 23 arrows)
            PuzzleLevel(
                levelNumber = 25,
                title = "Fortress Walls",
                rows = 6,
                cols = 6,
                arrows = listOf(
                    Arrow(1, 0, 0, Direction.UP, 0),
                    Arrow(2, 0, 1, Direction.UP, 1),
                    Arrow(3, 0, 4, Direction.UP, 2),
                    Arrow(4, 0, 5, Direction.UP, 3),
                    Arrow(5, 5, 0, Direction.DOWN, 0),
                    Arrow(6, 5, 1, Direction.DOWN, 1),
                    Arrow(7, 5, 4, Direction.DOWN, 2),
                    Arrow(8, 5, 5, Direction.DOWN, 3),
                    Arrow(9, 1, 2, Direction.UP, 0),
                    Arrow(10, 1, 3, Direction.UP, 1),
                    Arrow(11, 4, 2, Direction.DOWN, 2),
                    Arrow(12, 4, 3, Direction.DOWN, 3),
                    Arrow(13, 2, 0, Direction.LEFT, 0),
                    Arrow(14, 3, 0, Direction.LEFT, 1),
                    Arrow(15, 2, 5, Direction.RIGHT, 2),
                    Arrow(16, 3, 5, Direction.RIGHT, 3),
                    Arrow(17, 2, 1, Direction.LEFT, 0),
                    Arrow(18, 3, 1, Direction.LEFT, 1),
                    Arrow(19, 2, 4, Direction.RIGHT, 2),
                    Arrow(20, 3, 4, Direction.RIGHT, 3),
                    Arrow(21, 2, 2, Direction.LEFT, 0),
                    Arrow(22, 2, 3, Direction.RIGHT, 1),
                    Arrow(23, 3, 2, Direction.DOWN, 2)
                ),
                difficulty = Difficulty.HARD
            ),
            // Level 26: The Hive (6x6, 24 arrows)
            PuzzleLevel(
                levelNumber = 26,
                title = "The Hive",
                rows = 6,
                cols = 6,
                arrows = listOf(
                    Arrow(1, 0, 2, Direction.UP, 0),
                    Arrow(2, 0, 3, Direction.UP, 1),
                    Arrow(3, 1, 1, Direction.UP, 2),
                    Arrow(4, 1, 4, Direction.UP, 3),
                    Arrow(5, 2, 0, Direction.LEFT, 0),
                    Arrow(6, 3, 0, Direction.LEFT, 1),
                    Arrow(7, 2, 5, Direction.RIGHT, 2),
                    Arrow(8, 3, 5, Direction.RIGHT, 3),
                    Arrow(9, 4, 1, Direction.DOWN, 0),
                    Arrow(10, 4, 4, Direction.DOWN, 1),
                    Arrow(11, 5, 2, Direction.DOWN, 2),
                    Arrow(12, 5, 3, Direction.DOWN, 3),
                    Arrow(13, 1, 2, Direction.UP, 0),
                    Arrow(14, 1, 3, Direction.UP, 1),
                    Arrow(15, 2, 1, Direction.LEFT, 2),
                    Arrow(16, 3, 1, Direction.LEFT, 3),
                    Arrow(17, 2, 4, Direction.RIGHT, 0),
                    Arrow(18, 3, 4, Direction.RIGHT, 1),
                    Arrow(19, 4, 2, Direction.DOWN, 2),
                    Arrow(20, 4, 3, Direction.DOWN, 3),
                    Arrow(21, 2, 2, Direction.LEFT, 0),
                    Arrow(22, 2, 3, Direction.RIGHT, 1),
                    Arrow(23, 3, 2, Direction.LEFT, 2),
                    Arrow(24, 3, 3, Direction.RIGHT, 3)
                ),
                difficulty = Difficulty.EXPERT
            ),
            // Level 27: Constellation (6x6, 25 arrows)
            PuzzleLevel(
                levelNumber = 27,
                title = "Constellation",
                rows = 6,
                cols = 6,
                arrows = listOf(
                    Arrow(1, 0, 0, Direction.LEFT, 0),
                    Arrow(2, 0, 2, Direction.UP, 1),
                    Arrow(3, 0, 4, Direction.UP, 2),
                    Arrow(4, 0, 5, Direction.RIGHT, 3),
                    Arrow(5, 1, 1, Direction.UP, 0),
                    Arrow(6, 1, 3, Direction.UP, 1),
                    Arrow(7, 1, 5, Direction.RIGHT, 2),
                    Arrow(8, 2, 0, Direction.LEFT, 3),
                    Arrow(9, 2, 2, Direction.LEFT, 0),
                    Arrow(10, 2, 4, Direction.RIGHT, 1),
                    Arrow(11, 3, 1, Direction.LEFT, 2),
                    Arrow(12, 3, 3, Direction.RIGHT, 3),
                    Arrow(13, 3, 5, Direction.RIGHT, 0),
                    Arrow(14, 4, 0, Direction.LEFT, 1),
                    Arrow(15, 4, 2, Direction.DOWN, 2),
                    Arrow(16, 4, 4, Direction.DOWN, 3),
                    Arrow(17, 5, 0, Direction.LEFT, 0),
                    Arrow(18, 5, 1, Direction.DOWN, 1),
                    Arrow(19, 5, 3, Direction.DOWN, 2),
                    Arrow(20, 5, 5, Direction.RIGHT, 3),
                    Arrow(21, 1, 2, Direction.UP, 0),
                    Arrow(22, 2, 3, Direction.UP, 1),
                    Arrow(23, 3, 2, Direction.DOWN, 2),
                    Arrow(24, 4, 3, Direction.DOWN, 3),
                    Arrow(25, 2, 1, Direction.LEFT, 0)
                ),
                difficulty = Difficulty.EXPERT
            ),
            // Level 28: Cascade Wave (6x6, 26 arrows)
            PuzzleLevel(
                levelNumber = 28,
                title = "Cascade Wave",
                rows = 6,
                cols = 6,
                arrows = listOf(
                    Arrow(1, 0, 5, Direction.RIGHT, 0),
                    Arrow(2, 0, 4, Direction.RIGHT, 1),
                    Arrow(3, 0, 3, Direction.RIGHT, 2),
                    Arrow(4, 0, 2, Direction.RIGHT, 3),
                    Arrow(5, 0, 1, Direction.RIGHT, 0),
                    Arrow(6, 1, 5, Direction.RIGHT, 1),
                    Arrow(7, 1, 4, Direction.RIGHT, 2),
                    Arrow(8, 1, 3, Direction.RIGHT, 3),
                    Arrow(9, 1, 2, Direction.RIGHT, 0),
                    Arrow(10, 2, 5, Direction.RIGHT, 1),
                    Arrow(11, 2, 4, Direction.RIGHT, 2),
                    Arrow(12, 2, 3, Direction.RIGHT, 3),
                    Arrow(13, 3, 0, Direction.LEFT, 0),
                    Arrow(14, 3, 1, Direction.LEFT, 1),
                    Arrow(15, 3, 2, Direction.LEFT, 2),
                    Arrow(16, 4, 0, Direction.LEFT, 3),
                    Arrow(17, 4, 1, Direction.LEFT, 0),
                    Arrow(18, 4, 2, Direction.LEFT, 1),
                    Arrow(19, 4, 3, Direction.LEFT, 2),
                    Arrow(20, 5, 0, Direction.LEFT, 3),
                    Arrow(21, 5, 1, Direction.LEFT, 0),
                    Arrow(22, 5, 2, Direction.LEFT, 1),
                    Arrow(23, 5, 3, Direction.LEFT, 2),
                    Arrow(24, 5, 4, Direction.LEFT, 3),
                    Arrow(25, 2, 0, Direction.UP, 0),
                    Arrow(26, 3, 5, Direction.DOWN, 1)
                ),
                difficulty = Difficulty.EXPERT
            ),
            // Level 29: Grand Matrix (7x7, 28 arrows)
            PuzzleLevel(
                levelNumber = 29,
                title = "Grand Matrix",
                rows = 7,
                cols = 7,
                arrows = listOf(
                    Arrow(1, 0, 0, Direction.UP, 0),
                    Arrow(2, 0, 1, Direction.UP, 1),
                    Arrow(3, 0, 2, Direction.UP, 2),
                    Arrow(4, 0, 4, Direction.UP, 3),
                    Arrow(5, 0, 5, Direction.UP, 0),
                    Arrow(6, 0, 6, Direction.UP, 1),
                    Arrow(7, 1, 0, Direction.LEFT, 2),
                    Arrow(8, 2, 0, Direction.LEFT, 3),
                    Arrow(9, 4, 0, Direction.LEFT, 0),
                    Arrow(10, 5, 0, Direction.LEFT, 1),
                    Arrow(11, 1, 6, Direction.RIGHT, 2),
                    Arrow(12, 2, 6, Direction.RIGHT, 3),
                    Arrow(13, 4, 6, Direction.RIGHT, 0),
                    Arrow(14, 5, 6, Direction.RIGHT, 1),
                    Arrow(15, 6, 0, Direction.DOWN, 2),
                    Arrow(16, 6, 1, Direction.DOWN, 3),
                    Arrow(17, 6, 2, Direction.DOWN, 0),
                    Arrow(18, 6, 4, Direction.DOWN, 1),
                    Arrow(19, 6, 5, Direction.DOWN, 2),
                    Arrow(20, 6, 6, Direction.DOWN, 3),
                    Arrow(21, 2, 2, Direction.UP, 0),
                    Arrow(22, 2, 4, Direction.UP, 1),
                    Arrow(23, 3, 1, Direction.LEFT, 2),
                    Arrow(24, 3, 5, Direction.RIGHT, 3),
                    Arrow(25, 4, 2, Direction.DOWN, 0),
                    Arrow(26, 4, 4, Direction.DOWN, 1),
                    Arrow(27, 3, 2, Direction.LEFT, 2),
                    Arrow(28, 3, 4, Direction.RIGHT, 3)
                ),
                difficulty = Difficulty.EXPERT
            ),
            // Level 30: Master of Arrows (7x7, 30 arrows)
            PuzzleLevel(
                levelNumber = 30,
                title = "Master of Arrows",
                rows = 7,
                cols = 7,
                arrows = listOf(
                    Arrow(1, 0, 1, Direction.UP, 0),
                    Arrow(2, 0, 2, Direction.UP, 1),
                    Arrow(3, 0, 3, Direction.UP, 2),
                    Arrow(4, 0, 4, Direction.UP, 3),
                    Arrow(5, 0, 5, Direction.UP, 0),
                    Arrow(6, 1, 6, Direction.RIGHT, 1),
                    Arrow(7, 2, 6, Direction.RIGHT, 2),
                    Arrow(8, 3, 6, Direction.RIGHT, 3),
                    Arrow(9, 4, 6, Direction.RIGHT, 0),
                    Arrow(10, 5, 6, Direction.RIGHT, 1),
                    Arrow(11, 6, 5, Direction.DOWN, 2),
                    Arrow(12, 6, 4, Direction.DOWN, 3),
                    Arrow(13, 6, 3, Direction.DOWN, 0),
                    Arrow(14, 6, 2, Direction.DOWN, 1),
                    Arrow(15, 6, 1, Direction.DOWN, 2),
                    Arrow(16, 5, 0, Direction.LEFT, 3),
                    Arrow(17, 4, 0, Direction.LEFT, 0),
                    Arrow(18, 3, 0, Direction.LEFT, 1),
                    Arrow(19, 2, 0, Direction.LEFT, 2),
                    Arrow(20, 1, 0, Direction.LEFT, 3),
                    Arrow(21, 1, 2, Direction.UP, 0),
                    Arrow(22, 1, 3, Direction.UP, 1),
                    Arrow(23, 1, 4, Direction.UP, 2),
                    Arrow(24, 2, 5, Direction.RIGHT, 3),
                    Arrow(25, 4, 5, Direction.RIGHT, 0),
                    Arrow(26, 5, 4, Direction.DOWN, 1),
                    Arrow(27, 5, 3, Direction.DOWN, 2),
                    Arrow(28, 5, 2, Direction.DOWN, 3),
                    Arrow(29, 4, 1, Direction.LEFT, 0),
                    Arrow(30, 2, 1, Direction.LEFT, 1)
                ),
                difficulty = Difficulty.EXPERT
            )
        )
    }

    fun getLevel(levelNumber: Int): PuzzleLevel {
        val safeNum = levelNumber.coerceIn(1, levels.size)
        return levels[safeNum - 1]
    }
}
