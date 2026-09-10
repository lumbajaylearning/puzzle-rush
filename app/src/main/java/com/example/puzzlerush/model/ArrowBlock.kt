package com.example.puzzlerush.model

data class ArrowBlock(
    val id: Int,
    val row: Int,
    val col: Int,
    val direction: Direction,
    val colorIndex: Int = 0
)
