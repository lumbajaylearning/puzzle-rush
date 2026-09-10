package com.example.puzzlerush.model

enum class GameMode {
    CAMPAIGN,
    RUSH
}

data class PuzzleLevel(
    val levelNumber: Int,
    val title: String,
    val rows: Int,
    val cols: Int,
    val arrows: List<ArrowBlock>,
    val targetMoves: Int = arrows.size
)
