package com.navloka.arrowrush.core.scoring

object ScoreCalculator {

    /**
     * Determines the number of stars earned based on mistakes and move count.
     * 3 stars: optimal moves and 0 mistakes (mistakes == 0 && moves <= optimalMoves)
     * 2 stars: mistakes <= 2
     * 1 star: level completed
     */
    fun calculateStars(moves: Int, mistakes: Int, optimalMoves: Int): Int {
        return when {
            mistakes == 0 && moves <= optimalMoves -> 3
            mistakes <= 2 -> 2
            else -> 1
        }
    }

    /**
     * Calculates campaign level score points.
     */
    fun calculateLevelScore(
        stars: Int,
        moves: Int,
        mistakes: Int,
        optimalMoves: Int
    ): Int {
        val baseScore = stars * 1000
        val moveBonus = ((optimalMoves * 2 - moves) * 50).coerceAtLeast(0)
        val mistakePenalty = mistakes * 100
        return (baseScore + moveBonus - mistakePenalty).coerceAtLeast(100)
    }

    /**
     * Calculates points for a single arrow removal in Rush Mode with combo.
     */
    fun calculateRushMoveScore(combo: Int): Int {
        return 100 * combo.coerceAtLeast(1)
    }

    /**
     * Calculates the level clear bonus in Rush Mode.
     */
    fun calculateRushClearBonus(clearedCount: Int): Int {
        return 1000 + (clearedCount * 100)
    }
}
