package com.example.puzzlerush.model

enum class Direction(val dRow: Int, val dCol: Int, val angleDegrees: Float) {
    UP(-1, 0, 0f),
    RIGHT(0, 1, 90f),
    DOWN(1, 0, 180f),
    LEFT(0, -1, 270f);

    val label: String
        get() = when (this) {
            UP -> "Up"
            RIGHT -> "Right"
            DOWN -> "Down"
            LEFT -> "Left"
        }
}
