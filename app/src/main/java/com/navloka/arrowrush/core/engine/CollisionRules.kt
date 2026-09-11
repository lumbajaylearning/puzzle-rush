package com.navloka.arrowrush.core.engine

import com.navloka.arrowrush.core.model.Arrow
import com.navloka.arrowrush.core.model.Board
import com.navloka.arrowrush.core.model.Position

object CollisionRules {

    /**
     * Traces forward along the arrow's direction from its adjacent cell to the board edge.
     * Returns the first Arrow encountered, or null if the path is clear out of the board.
     */
    fun findBlockingArrow(arrow: Arrow, board: Board): Arrow? {
        var current: Position = arrow.position + arrow.direction
        while (board.isInside(current)) {
            val obstacle = board.getArrowAt(current)
            if (obstacle != null) {
                return obstacle
            }
            current += arrow.direction
        }
        return null
    }

    /**
     * Checks whether an arrow has an unobstructed path to exit the board.
     */
    fun canEscape(arrow: Arrow, board: Board): Boolean =
        findBlockingArrow(arrow, board) == null
}
