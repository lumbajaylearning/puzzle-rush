package com.navloka.arrowrush.core.generator

import com.navloka.arrowrush.core.engine.CollisionRules
import com.navloka.arrowrush.core.engine.LevelValidator
import com.navloka.arrowrush.core.model.Difficulty
import com.navloka.arrowrush.core.model.PuzzleLevel

object DifficultyEvaluator {

    data class LevelMetrics(
        val boardSize: Int,
        val arrowCount: Int,
        val density: Float,
        val initialAvailableMoves: Int,
        val dependencyDepth: Int,
        val difficultyScore: Float
    )

    fun evaluate(level: PuzzleLevel): Difficulty {
        val metrics = calculateMetrics(level)
        return when {
            metrics.difficultyScore < 20f -> Difficulty.EASY
            metrics.difficultyScore < 45f -> Difficulty.MEDIUM
            metrics.difficultyScore < 75f -> Difficulty.HARD
            else -> Difficulty.EXPERT
        }
    }

    fun calculateMetrics(level: PuzzleLevel): LevelMetrics {
        val boardSize = level.rows * level.cols
        val arrowCount = level.arrows.size
        val density = if (boardSize > 0) arrowCount.toFloat() / boardSize else 0f

        val board = level.createInitialBoard()
        val initialAvailable = board.arrows.values
            .count { CollisionRules.canEscape(it, board) }

        val validator = LevelValidator()
        val sequence = validator.solutionSequence(level)
        val depth = if (sequence != null && initialAvailable > 0) {
            (arrowCount.toFloat() / (initialAvailable + 1) * 2f).toInt()
        } else {
            arrowCount
        }

        val constraintFactor = if (initialAvailable > 0) (arrowCount.toFloat() / initialAvailable) else 10f
        val difficultyScore = (arrowCount * 1.5f) +
                (boardSize * 0.4f) +
                (density * 20f) +
                (constraintFactor * 1.8f) +
                (depth * 2f)

        return LevelMetrics(
            boardSize = boardSize,
            arrowCount = arrowCount,
            density = density,
            initialAvailableMoves = initialAvailable,
            dependencyDepth = depth,
            difficultyScore = difficultyScore
        )
    }
}
