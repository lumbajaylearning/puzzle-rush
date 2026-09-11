package com.navloka.arrowrush.model

import com.navloka.arrowrush.core.model.Arrow
import com.navloka.arrowrush.core.model.Board
import com.navloka.arrowrush.core.model.PuzzleLevel
import com.navloka.arrowrush.core.scoring.FocusRules

/**
 * UI presentation state representing the game screen.
 * Encapsulates the engine's pure Board state alongside transient UI & animation properties.
 */
data class GameState(
    val level: PuzzleLevel,
    val mode: GameMode = GameMode.CAMPAIGN,
    val board: Board = level.createInitialBoard(),
    // Animation & transient interaction states
    val flyingArrowIds: Set<Int> = emptySet(),
    val blockedArrowId: Int? = null,
    val blockerArrowId: Int? = null,
    val hintArrowId: Int? = null,
    val movesCount: Int = 0,
    val mistakesCount: Int = 0,
    val focusRemaining: Int = FocusRules.MAX_FOCUS,
    val combo: Int = 0,
    val isCleared: Boolean = false,
    val isLevelFailed: Boolean = false,
    val starsEarned: Int = 0,
    val canUndo: Boolean = false,
    val feedbackMessage: String? = null,
    // Rush mode state
    val rushTimeRemainingSeconds: Int = 60,
    val rushScore: Int = 0,
    val rushLevelsCleared: Int = 0,
    val isRushGameOver: Boolean = false
) {
    /**
     * Map of currently active arrows on the board, provided for UI rendering.
     */
    val activeArrows: Map<Int, Arrow>
        get() = board.arrows
}
