package com.navloka.arrowrush.core.model

data class Board(
    val rows: Int,
    val cols: Int,
    val arrows: Map<Int, Arrow>
) {
    val occupiedPositions: Set<Position> by lazy {
        arrows.values.map { it.position }.toSet()
    }

    private val positionToArrow: Map<Position, Arrow> by lazy {
        arrows.values.associateBy { it.position }
    }

    fun isInside(position: Position): Boolean =
        position.row in 0 until rows && position.col in 0 until cols

    fun isInside(row: Int, col: Int): Boolean =
        row in 0 until rows && col in 0 until cols

    val size: Int get() = arrows.size
    val isEmpty: Boolean get() = arrows.isEmpty()

    fun getArrow(id: Int): Arrow? = arrows[id]

    fun getArrowAt(position: Position): Arrow? = positionToArrow[position]

    fun getArrowAt(row: Int, col: Int): Arrow? = positionToArrow[Position(row, col)]

    fun withArrowRemoved(id: Int): Board =
        copy(arrows = arrows - id)

    fun withArrowAdded(arrow: Arrow): Board =
        copy(arrows = arrows + (arrow.id to arrow))
}
