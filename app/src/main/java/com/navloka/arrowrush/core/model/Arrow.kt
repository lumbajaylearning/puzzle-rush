package com.navloka.arrowrush.core.model

data class Arrow(
    val id: Int,
    val position: Position,
    val direction: Direction,
    val colorIndex: Int = 0
) {
    constructor(id: Int, row: Int, col: Int, direction: Direction, colorIndex: Int = 0) :
            this(id, Position(row, col), direction, colorIndex)

    val row: Int get() = position.row
    val col: Int get() = position.col
}
