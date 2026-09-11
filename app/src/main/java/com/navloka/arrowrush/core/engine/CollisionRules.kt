package com.navloka.arrowrush.core.engine

import com.navloka.arrowrush.core.model.Arrow
import com.navloka.arrowrush.core.model.Board
import com.navloka.arrowrush.core.model.Position

object CollisionRules {

    /** Simulates the complete path translating one cell at a time until it leaves the board. */
    fun findBlockingArrow(arrow: Arrow, board: Board): Arrow? {
        var rowOffset = arrow.direction.dRow
        var colOffset = arrow.direction.dCol

        while (arrow.path.any { cell ->
                board.isInside(cell.row + rowOffset, cell.col + colOffset)
            }) {
            for (cell in arrow.path) {
                val translated = Position(cell.row + rowOffset, cell.col + colOffset)
                if (!board.isInside(translated)) continue

                val obstacle = board.getArrowAt(translated)
                if (obstacle != null && obstacle.id != arrow.id) {
                    return obstacle
                }
            }
            rowOffset += arrow.direction.dRow
            colOffset += arrow.direction.dCol
        }
        return null
    }

    /**
     * Checks whether an arrow has an unobstructed path to exit the board.
     */
    fun canEscape(arrow: Arrow, board: Board): Boolean =
        findBlockingArrow(arrow, board) == null
}
