package com.navloka.arrowrush.core.model

enum class Direction(val dRow: Int, val dCol: Int, val angleDegrees: Float) {
    // ArrowBoardView draws its base arrow facing right. Keep visual rotation and movement
    // deltas together here so the arrow always flies in the direction it points.
    UP(-1, 0, -90f),
    RIGHT(0, 1, 0f),
    DOWN(1, 0, 90f),
    LEFT(0, -1, 180f);

    val label: String
        get() = when (this) {
            UP -> "Up"
            RIGHT -> "Right"
            DOWN -> "Down"
            LEFT -> "Left"
        }
}
