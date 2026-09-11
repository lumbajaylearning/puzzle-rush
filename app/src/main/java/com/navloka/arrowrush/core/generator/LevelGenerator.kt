package com.navloka.arrowrush.core.generator

import com.navloka.arrowrush.core.engine.CollisionRules
import com.navloka.arrowrush.core.engine.LevelValidator
import com.navloka.arrowrush.core.model.Arrow
import com.navloka.arrowrush.core.model.Board
import com.navloka.arrowrush.core.model.Difficulty
import com.navloka.arrowrush.core.model.Direction
import com.navloka.arrowrush.core.model.Position
import com.navloka.arrowrush.core.model.PuzzleLevel
import java.util.Random

class LevelGenerator(
    private val random: Random = Random(),
    private val validator: LevelValidator = LevelValidator()
) {

    /**
     * Generates a puzzle level by constructing backwards from an empty (solved) board.
     * Every placed arrow is guaranteed to have a clear exit path at insertion time,
     * which mathematically guarantees that the generated level is solvable.
     */
    fun generate(levelNumber: Int, difficulty: Difficulty): PuzzleLevel {
        val (rows, cols, arrows) = when (difficulty) {
            Difficulty.EASY -> Triple(4, 4, 6)
            Difficulty.MEDIUM -> Triple(5, 5, 10)
            Difficulty.HARD -> Triple(6, 6, 16)
            Difficulty.EXPERT -> Triple(7, 7, 22)
        }
        return generateLevel(
            levelNumber = levelNumber,
            title = "Level $levelNumber",
            rows = rows,
            cols = cols,
            targetArrowCount = arrows
        )
    }

    fun generateLevel(
        levelNumber: Int,
        title: String = "Generated Level $levelNumber",
        rows: Int = 5,
        cols: Int = 5,
        targetArrowCount: Int = 12,
        maxAttempts: Int = 15
    ): PuzzleLevel {
        for (attempt in 0 until maxAttempts) {
            val generated = tryGenerateBackwards(levelNumber, title, rows, cols, targetArrowCount)
            if (generated != null && validator.isSolvable(generated)) {
                val difficulty = DifficultyEvaluator.evaluate(generated)
                return generated.copy(
                    difficulty = difficulty,
                    optimalMoves = generated.arrows.size
                )
            }
        }

        // Fallback: minimal guaranteed level if random attempts exhausted
        val fallbackArrows = listOf(
            Arrow(1, Position(0, 0), Direction.UP, 0),
            Arrow(2, Position(1, 0), Direction.UP, 1),
            Arrow(3, Position(1, 1), Direction.RIGHT, 2)
        )
        val fallback = PuzzleLevel(
            levelNumber = levelNumber,
            title = title,
            rows = maxOf(rows, 3),
            cols = maxOf(cols, 3),
            arrows = fallbackArrows
        )
        return fallback.copy(
            difficulty = DifficultyEvaluator.evaluate(fallback),
            optimalMoves = fallback.arrows.size
        )
    }

    private fun tryGenerateBackwards(
        levelNumber: Int,
        title: String,
        rows: Int,
        cols: Int,
        targetCount: Int
    ): PuzzleLevel? {
        var currentBoard = Board(rows, cols, emptyMap())
        val placedArrows = mutableListOf<Arrow>()
        val directions = Direction.entries.toTypedArray()
        var arrowIdCounter = 1

        val allPositions = mutableListOf<Position>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                allPositions.add(Position(r, c))
            }
        }

        for (step in 0 until targetCount) {
            // Find all valid candidate (position, direction) placements that can escape the current board
            val candidates = mutableListOf<Pair<Position, Direction>>()
            val unoccupiedPositions = allPositions.filter { currentBoard.getArrowAt(it) == null }

            if (unoccupiedPositions.isEmpty()) break

            for (pos in unoccupiedPositions) {
                for (dir in directions) {
                    val candidateArrow = Arrow(arrowIdCounter, pos, dir, arrowIdCounter % 4)
                    if (CollisionRules.canEscape(candidateArrow, currentBoard)) {
                        candidates.add(pos to dir)
                    }
                }
            }

            if (candidates.isEmpty()) break

            val (chosenPos, chosenDir) = candidates[random.nextInt(candidates.size)]
            val newArrow = Arrow(
                id = arrowIdCounter++,
                position = chosenPos,
                direction = chosenDir,
                colorIndex = (arrowIdCounter - 1) % 4
            )

            placedArrows.add(newArrow)
            currentBoard = currentBoard.withArrowAdded(newArrow)
        }

        if (placedArrows.size < 3) return null

        return PuzzleLevel(
            levelNumber = levelNumber,
            title = title,
            rows = rows,
            cols = cols,
            arrows = placedArrows,
            optimalMoves = placedArrows.size
        )
    }
}
