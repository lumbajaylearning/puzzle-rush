package com.navloka.arrowrush.core.model

data class Arrow(
    val id: Int,
    val position: Position,
    val direction: Direction,
    val colorIndex: Int = 0,
    /** Ordered tail-to-head cells occupied by this arrow path. */
    val path: List<Position> = listOf(position)
) {
    constructor(id: Int, row: Int, col: Int, direction: Direction, colorIndex: Int = 0) :
        this(id, Position(row, col), direction, colorIndex)

    constructor(id: Int, path: List<Position>, direction: Direction, colorIndex: Int = 0) :
        this(id, path.last(), direction, colorIndex, path)

    init {
        require(path.isNotEmpty()) { "Arrow path cannot be empty" }
        require(position == path.last()) { "Arrow position must be the path head" }
        require(path.distinct().size == path.size) { "Arrow path cannot cross itself" }
        require(path.zipWithNext().all { (a, b) ->
            kotlin.math.abs(a.row - b.row) + kotlin.math.abs(a.col - b.col) == 1
        }) { "Arrow path cells must be orthogonally adjacent" }
    }

    val row: Int get() = position.row
    val col: Int get() = position.col
    val occupiedPositions: Set<Position> get() = path.toSet()
}
