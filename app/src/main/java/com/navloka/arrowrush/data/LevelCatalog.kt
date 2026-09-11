package com.navloka.arrowrush.data

import com.navloka.arrowrush.core.engine.CollisionRules
import com.navloka.arrowrush.core.model.Arrow
import com.navloka.arrowrush.core.model.Board
import com.navloka.arrowrush.core.model.Difficulty
import com.navloka.arrowrush.core.model.Direction
import com.navloka.arrowrush.core.model.Position
import com.navloka.arrowrush.core.model.PuzzleLevel
import kotlin.random.Random

/** Deterministic campaign made from fewer, longer, interlocking arrow paths. */
object LevelCatalog {

    private val titles = listOf(
        "First Flight", "Outward Bound", "Crossroad Peek", "The Pinwheel", "Corner Lock",
        "Traffic Knot", "Outer Peeling", "The Zipper", "Crossroads", "Diamond Core",
        "Boxed In", "Spiral Step", "Twin Corridors", "The Ring", "Vortex",
        "Interlocking Teeth", "The Cascade", "Symmetry Trap", "Hourglass Matrix",
        "Crossroads Central", "The Labyrinth", "Twin Spirals", "Checker Rush", "Whirlpool",
        "Fortress Walls", "The Hive", "Constellation", "Cascade Wave", "Grand Matrix",
        "Master of Arrows"
    )

    val levels: List<PuzzleLevel> by lazy {
        listOf(firstFlight()) + (2..30).map(::generatePathLevel)
    }

    fun getLevel(levelNumber: Int): PuzzleLevel =
        levels[(levelNumber - 1).coerceIn(levels.indices)]

    private fun firstFlight() = PuzzleLevel(
        levelNumber = 1,
        title = titles.first(),
        rows = 7,
        cols = 5,
        arrows = listOf(
            Arrow(1, listOf(Position(1, 2), Position(0, 2)), Direction.UP, 0),
            Arrow(2, listOf(Position(3, 2), Position(2, 2)), Direction.UP, 1),
            Arrow(3, listOf(Position(5, 2), Position(4, 2)), Direction.UP, 2)
        ),
        difficulty = Difficulty.EASY
    )

    private fun generatePathLevel(levelNumber: Int): PuzzleLevel {
        val size = boardSize(levelNumber)
        val targetCount = pieceCount(levelNumber)
        val maxLength = maxPathLength(levelNumber)

        repeat(MAX_LEVEL_ATTEMPTS) { levelAttempt ->
            val random = Random(levelNumber * 104_729 + levelAttempt * 7_919)
            val arrows = mutableListOf<Arrow>()
            val occupied = mutableSetOf<Position>()

            for (id in 1..targetCount) {
                val arrow = findPathCandidate(
                    id = id,
                    levelNumber = levelNumber,
                    rows = size,
                    cols = size,
                    maxLength = maxLength,
                    existing = arrows,
                    occupied = occupied,
                    random = random
                ) ?: break

                arrows += arrow
                occupied += arrow.path
            }

            if (arrows.size == targetCount && followsAuthoredOrder(arrows, size, size)) {
                return PuzzleLevel(
                    levelNumber = levelNumber,
                    title = titles[levelNumber - 1],
                    rows = size,
                    cols = size,
                    arrows = arrows,
                    difficulty = difficultyFor(levelNumber)
                )
            }
        }

        error("Unable to generate deterministic path puzzle for level $levelNumber")
    }

    private fun findPathCandidate(
        id: Int,
        levelNumber: Int,
        rows: Int,
        cols: Int,
        maxLength: Int,
        existing: List<Arrow>,
        occupied: Set<Position>,
        random: Random
    ): Arrow? {
        repeat(MAX_CANDIDATE_ATTEMPTS) {
            val direction = Direction.entries.random(random)
            val head = if (existing.isEmpty()) {
                Position(random.nextInt(rows), random.nextInt(cols))
            } else {
                // Put the new head behind an older path so it begins blocked and becomes
                // available only after an earlier dependency has escaped.
                val blockerCell = occupied.random(random)
                val distance = random.nextInt(1, minOf(rows, cols))
                Position(
                    blockerCell.row - direction.dRow * distance,
                    blockerCell.col - direction.dCol * distance
                )
            }

            val previous = Position(
                head.row - direction.dRow,
                head.col - direction.dCol
            )
            if (!inside(head, rows, cols) || !inside(previous, rows, cols)) return@repeat
            if (head in occupied || previous in occupied) return@repeat

            val desiredLength = random.nextInt(2, maxLength + 1)
            val headToTail = mutableListOf(head, previous)
            while (headToTail.size < desiredLength) {
                val current = headToTail.last()
                val candidates = Direction.entries
                    .map { current + it }
                    .filter { cell ->
                        inside(cell, rows, cols) && cell !in occupied && cell !in headToTail
                    }
                if (candidates.isEmpty()) break

                // A turn makes the piece visually read as a path rather than a long tap target.
                val straightBack = Position(
                    current.row - direction.dRow,
                    current.col - direction.dCol
                )
                val turning = candidates.filter { it != straightBack }
                headToTail += (turning.ifEmpty { candidates }).random(random)
            }

            val path = headToTail.asReversed()
            val candidate = Arrow(
                id = id,
                path = path,
                direction = direction,
                colorIndex = (id + levelNumber) % 4
            )
            val tentative = existing + candidate
            val board = Board(rows, cols, tentative.associateBy { it.id })
            val beginsBlocked = id == 1 || CollisionRules.findBlockingArrow(candidate, board) != null

            if (beginsBlocked && followsAuthoredOrder(tentative, rows, cols)) {
                return candidate
            }
        }
        return null
    }

    /** IDs are the intended dependency order; each accepted addition must preserve it. */
    private fun followsAuthoredOrder(arrows: List<Arrow>, rows: Int, cols: Int): Boolean {
        var board = Board(rows, cols, arrows.associateBy { it.id })
        for (arrow in arrows.sortedBy { it.id }) {
            val current = board.getArrow(arrow.id) ?: return false
            if (!CollisionRules.canEscape(current, board)) return false
            board = board.withArrowRemoved(arrow.id)
        }
        return board.isEmpty
    }

    private fun boardSize(levelNumber: Int): Int = when (levelNumber) {
        in 1..5 -> 6
        in 6..11 -> 7
        in 12..18 -> 8
        in 19..24 -> 9
        else -> 10
    }

    private fun pieceCount(levelNumber: Int): Int = when (levelNumber) {
        in 1..3 -> 5
        in 4..6 -> 6
        in 7..9 -> 7
        in 10..12 -> 8
        in 13..15 -> 9
        in 16..18 -> 10
        in 19..21 -> 11
        in 22..24 -> 12
        in 25..27 -> 13
        else -> 14
    }

    private fun maxPathLength(levelNumber: Int): Int = when (levelNumber) {
        in 1..6 -> 3
        in 7..15 -> 4
        in 16..24 -> 5
        else -> 6
    }

    private fun difficultyFor(levelNumber: Int): Difficulty = when (levelNumber) {
        in 1..8 -> Difficulty.EASY
        in 9..16 -> Difficulty.MEDIUM
        in 17..24 -> Difficulty.HARD
        else -> Difficulty.EXPERT
    }

    private fun inside(position: Position, rows: Int, cols: Int): Boolean =
        position.row in 0 until rows && position.col in 0 until cols

    private const val MAX_LEVEL_ATTEMPTS = 80
    private const val MAX_CANDIDATE_ATTEMPTS = 2_000
}
