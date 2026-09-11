package com.navloka.arrowrush.data

import com.navloka.arrowrush.core.model.Arrow
import com.navloka.arrowrush.core.model.Difficulty
import com.navloka.arrowrush.core.model.Direction
import com.navloka.arrowrush.core.model.Position
import com.navloka.arrowrush.core.model.PuzzleLevel
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

/**
 * The campaign starts with one hand-authored teaching board, then builds increasingly dense
 * dependency mazes. Every generated direction is chosen while removing arrows from a temporary
 * board, so replaying those removals in id order is always a valid solution.
 *
 * Keeping this deterministic gives every player the same handcrafted-feeling campaign and lets
 * the validator protect the whole catalog in unit tests.
 */
object LevelCatalog {

    private val titles = listOf(
        "First Flight",
        "Outward Bound",
        "Crossroad Peek",
        "The Pinwheel",
        "Corner Lock",
        "Traffic Knot",
        "Outer Peeling",
        "The Zipper",
        "Crossroads",
        "Diamond Core",
        "Boxed In",
        "Spiral Step",
        "Twin Corridors",
        "The Ring",
        "Vortex",
        "Interlocking Teeth",
        "The Cascade",
        "Symmetry Trap",
        "Hourglass Matrix",
        "Crossroads Central",
        "The Labyrinth",
        "Twin Spirals",
        "Checker Rush",
        "Whirlpool",
        "Fortress Walls",
        "The Hive",
        "Constellation",
        "Cascade Wave",
        "Grand Matrix",
        "Master of Arrows"
    )

    val levels: List<PuzzleLevel> by lazy {
        listOf(firstFlight()) + (2..30).map(::buildDenseLevel)
    }

    fun getLevel(levelNumber: Int): PuzzleLevel =
        levels[(levelNumber - 1).coerceIn(levels.indices)]

    private fun firstFlight() = PuzzleLevel(
        levelNumber = 1,
        title = titles.first(),
        rows = 3,
        cols = 3,
        arrows = listOf(
            Arrow(1, 1, 2, Direction.RIGHT, 0),
            Arrow(2, 1, 1, Direction.RIGHT, 1),
            Arrow(3, 1, 0, Direction.RIGHT, 2)
        ),
        difficulty = Difficulty.EASY
    )

    private fun buildDenseLevel(levelNumber: Int): PuzzleLevel {
        val size = boardSize(levelNumber)
        val arrowCount = arrowCount(levelNumber)
        val random = Random(levelNumber * 7_919 + 41)
        val positions = selectPositions(
            rows = size,
            cols = size,
            count = arrowCount,
            pattern = levelNumber % 5,
            random = random
        )

        val remaining = positions.toMutableSet()
        val removed = mutableSetOf<Position>()
        val arrows = mutableListOf<Arrow>()

        while (remaining.isNotEmpty()) {
            val candidates = remaining.flatMap { position ->
                Direction.entries.mapNotNull { direction ->
                    val ray = rayFrom(position, direction, size, size)
                    if (ray.any { it in remaining }) {
                        null
                    } else {
                        Candidate(
                            position = position,
                            direction = direction,
                            // Prefer paths opened by earlier removals. This creates long chains
                            // instead of a board where every edge arrow is immediately free.
                            dependencyScore = ray.count { it in removed } * 100 + ray.size
                        )
                    }
                }
            }

            // A finite board always has at least one outward-facing candidate.
            val bestScore = candidates.maxOf { it.dependencyScore }
            val strongest = candidates.filter { it.dependencyScore >= bestScore - 1 }
            val chosen = strongest[random.nextInt(strongest.size)]
            val id = arrows.size + 1
            arrows += Arrow(
                id = id,
                row = chosen.position.row,
                col = chosen.position.col,
                direction = chosen.direction,
                colorIndex = (id + levelNumber) % 4
            )
            remaining.remove(chosen.position)
            removed += chosen.position
        }

        return PuzzleLevel(
            levelNumber = levelNumber,
            title = titles[levelNumber - 1],
            rows = size,
            cols = size,
            arrows = arrows,
            difficulty = difficultyFor(levelNumber)
        )
    }

    private fun boardSize(levelNumber: Int): Int = when (levelNumber) {
        in 1..3 -> 3
        in 4..8 -> 4
        in 9..16 -> 5
        in 17..24 -> 6
        else -> 7
    }

    private fun arrowCount(levelNumber: Int): Int = when (levelNumber) {
        1 -> 3
        2 -> 7
        3 -> 9
        in 4..8 -> levelNumber + 7 // 11 ... 15 of 16 cells
        in 9..16 -> levelNumber + 8 // 17 ... 24 of 25 cells
        in 17..24 -> levelNumber + 8 // 25 ... 32 of 36 cells
        else -> 34 + (levelNumber - 25) * 2 // 34 ... 44 of 49 cells
    }

    private fun difficultyFor(levelNumber: Int): Difficulty = when (levelNumber) {
        in 1..8 -> Difficulty.EASY
        in 9..16 -> Difficulty.MEDIUM
        in 17..24 -> Difficulty.HARD
        else -> Difficulty.EXPERT
    }

    private fun selectPositions(
        rows: Int,
        cols: Int,
        count: Int,
        pattern: Int,
        random: Random
    ): Set<Position> {
        val all = (0 until rows).flatMap { row ->
            (0 until cols).map { col -> Position(row, col) }
        }
        val centerRow = (rows - 1) / 2f
        val centerCol = (cols - 1) / 2f

        return all
            .map { position ->
                val rowDistance = abs(position.row - centerRow)
                val colDistance = abs(position.col - centerCol)
                val shapeScore = when (pattern) {
                    0 -> rowDistance + colDistance // diamond
                    1 -> min(rowDistance, colDistance) // cross and corridors
                    2 -> min(
                        abs(position.row - position.col).toFloat(),
                        abs(position.row + position.col - (cols - 1)).toFloat()
                    ) // twin diagonals
                    3 -> minOf(
                        position.row,
                        position.col,
                        rows - 1 - position.row,
                        cols - 1 - position.col
                    ).toFloat() // outer ring
                    else -> abs(rowDistance - colDistance) // hourglass corners
                }
                RankedPosition(position, shapeScore, random.nextInt(10_000))
            }
            .sortedWith(compareBy<RankedPosition> { it.shapeScore }.thenBy { it.tieBreaker })
            .take(count)
            .mapTo(linkedSetOf()) { it.position }
    }

    private fun rayFrom(
        start: Position,
        direction: Direction,
        rows: Int,
        cols: Int
    ): List<Position> = buildList {
        var position = start + direction
        while (position.row in 0 until rows && position.col in 0 until cols) {
            add(position)
            position += direction
        }
    }

    private data class Candidate(
        val position: Position,
        val direction: Direction,
        val dependencyScore: Int
    )

    private data class RankedPosition(
        val position: Position,
        val shapeScore: Float,
        val tieBreaker: Int
    )
}
