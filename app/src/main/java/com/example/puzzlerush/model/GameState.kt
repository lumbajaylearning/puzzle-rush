package com.example.puzzlerush.model

data class GameState(
    val level: PuzzleLevel,
    val mode: GameMode = GameMode.CAMPAIGN,
    val activeArrows: Map<Int, ArrowBlock> = emptyMap(),
    val flyingArrowIds: Set<Int> = emptySet(),
    val blockedArrowId: Int? = null,
    val blockerArrowId: Int? = null,
    val hintArrowId: Int? = null,
    val movesCount: Int = 0,
    val mistakesCount: Int = 0,
    val isCleared: Boolean = false,
    val starsEarned: Int = 0,
    val undoStack: List<ArrowBlock> = emptyList(),
    val rushTimeRemainingSeconds: Int = 60,
    val rushScore: Int = 0,
    val rushCombo: Int = 0,
    val rushLevelsCleared: Int = 0,
    val isRushGameOver: Boolean = false
)
