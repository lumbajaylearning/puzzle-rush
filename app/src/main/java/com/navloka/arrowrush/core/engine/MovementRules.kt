package com.navloka.arrowrush.core.engine

import com.navloka.arrowrush.core.model.Arrow
import com.navloka.arrowrush.core.model.Board

object MovementRules {

    fun canMove(arrowId: Int, board: Board): Boolean {
        val arrow = board.getArrow(arrowId) ?: return false
        return CollisionRules.canEscape(arrow, board)
    }

    fun getBlockingArrow(arrowId: Int, board: Board): Arrow? {
        val arrow = board.getArrow(arrowId) ?: return null
        return CollisionRules.findBlockingArrow(arrow, board)
    }

    fun getAvailableMoves(board: Board): List<Int> {
        return board.arrows.values
            .filter { CollisionRules.canEscape(it, board) }
            .map { it.id }
    }
}
