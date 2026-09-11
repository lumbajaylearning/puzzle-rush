package com.navloka.arrowrush.core.engine

import com.navloka.arrowrush.core.model.Board

object WinDetector {
    fun isSolved(board: Board): Boolean = board.arrows.isEmpty()
}
