package com.navloka.arrowrush.core.model

data class PuzzleLevel(
    val levelNumber: Int,
    val title: String,
    val rows: Int,
    val cols: Int,
    val arrows: List<Arrow>,
    val difficulty: Difficulty = Difficulty.EASY,
    val optimalMoves: Int = arrows.size
) {
    fun createInitialBoard(): Board =
        Board(rows = rows, cols = cols, arrows = arrows.associateBy { it.id })
}
