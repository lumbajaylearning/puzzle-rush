package com.navloka.arrowrush.core.model

data class Position(val row: Int, val col: Int) {
    operator fun plus(direction: Direction): Position =
        Position(row + direction.dRow, col + direction.dCol)
}
