package com.navloka.arrowrush.core.engine

import com.navloka.arrowrush.core.model.Arrow
import com.navloka.arrowrush.core.model.Board
import com.navloka.arrowrush.core.model.Position
import com.navloka.arrowrush.core.model.PuzzleLevel

sealed class MoveResult {
    data class Success(
        val movedArrow: Arrow,
        val remainingCount: Int,
        val isSolved: Boolean
    ) : MoveResult()

    data class Blocked(
        val arrow: Arrow,
        val blockingArrow: Arrow
    ) : MoveResult()

    data class Invalid(
        val reason: String
    ) : MoveResult()
}

/**
 * Feedback interface triggered during movement execution in [PuzzleEngine].
 * Receives notifications when an arrow is blocked by an obstacle in its line of travel,
 * or when an unobstructed arrow performs its off-board movement.
 */
interface MovementFeedbackListener {
    /**
     * Triggered when an arrow's trajectory is blocked by another arrow on the grid.
     */
    fun onMoveBlocked(arrow: Arrow, blockingArrow: Arrow)

    /**
     * Triggered when an arrow has an open path out of the board and executes its off-board movement.
     */
    fun onMoveSuccess(arrow: Arrow, remainingCount: Int, isSolved: Boolean) {}
}

class PuzzleEngine(
    initialLevel: PuzzleLevel? = null,
    var feedbackListener: MovementFeedbackListener? = null
) {

    private var currentLevel: PuzzleLevel? = null
    private var board: Board = Board(0, 0, emptyMap())
    private val undoStack: MutableList<Arrow> = mutableListOf()
    private var moveCount: Int = 0
    private var mistakeCount: Int = 0

    init {
        if (initialLevel != null) {
            loadLevel(initialLevel)
        }
    }

    fun loadLevel(level: PuzzleLevel) {
        currentLevel = level
        board = level.createInitialBoard()
        undoStack.clear()
        moveCount = 0
        mistakeCount = 0
    }

    /**
     * Strictly enforces the 'trace along direction' rule:
     * Starting from the cell immediately ahead of [arrow] in its direction of travel,
     * traces forward cell-by-cell in `arrow.direction` until exiting the board boundary.
     *
     * If ANY arrow exists on the grid in the direction of travel, returns that blocking Arrow.
     * If no arrow exists along the line of travel to the board edge, returns null (clear path to escape).
     */
    fun traceAlongDirection(arrow: Arrow): Arrow? {
        var current: Position = arrow.position + arrow.direction
        while (board.isInside(current)) {
            val obstacle = board.getArrowAt(current)
            if (obstacle != null) {
                return obstacle
            }
            current = current + arrow.direction
        }
        return null
    }

    fun canMove(arrowId: Int): Boolean {
        val arrow = board.getArrow(arrowId) ?: return false
        return traceAlongDirection(arrow) == null
    }

    fun getBlockingArrow(arrowId: Int): Arrow? {
        val arrow = board.getArrow(arrowId) ?: return null
        return traceAlongDirection(arrow)
    }

    /**
     * Attempts to move the specified arrow according to the 'trace along direction' rule:
     * 1. If any arrow exists on the grid in the direction of travel, sets status to blocked
     *    and triggers the feedback interface ([MovementFeedbackListener.onMoveBlocked]).
     * 2. Otherwise, performs off-board movement/animation, updating board state,
     *    undo stack, and move count, and triggers [MovementFeedbackListener.onMoveSuccess].
     */
    fun move(arrowId: Int): MoveResult {
        val arrow = board.getArrow(arrowId)
            ?: return MoveResult.Invalid("Arrow with ID $arrowId not found on board")

        // Strictly enforce 'trace along direction'
        val blockingArrow = traceAlongDirection(arrow)
        if (blockingArrow != null) {
            mistakeCount++
            // Trigger the feedback interface for blocked move
            feedbackListener?.onMoveBlocked(arrow, blockingArrow)
            return MoveResult.Blocked(arrow, blockingArrow)
        }

        // Otherwise: no obstacles along direction of travel, perform off-board movement
        board = board.withArrowRemoved(arrowId)
        undoStack.add(arrow)
        moveCount++
        val solved = isSolved()

        // Trigger the feedback interface for off-board movement
        feedbackListener?.onMoveSuccess(arrow, board.arrows.size, solved)

        return MoveResult.Success(
            movedArrow = arrow,
            remainingCount = board.arrows.size,
            isSolved = solved
        )
    }

    fun undo(): Arrow? {
        if (undoStack.isEmpty()) return null
        val restoredArrow = undoStack.removeAt(undoStack.lastIndex)
        board = board.withArrowAdded(restoredArrow)
        moveCount = (moveCount - 1).coerceAtLeast(0)
        return restoredArrow
    }

    fun isSolved(): Boolean = WinDetector.isSolved(board)

    fun canUndo(): Boolean = undoStack.isNotEmpty()

    fun getRemainingArrowCount(): Int = board.size

    fun getAvailableMoves(): List<Int> =
        board.arrows.values
            .filter { traceAlongDirection(it) == null }
            .map { it.id }

    fun getBoardState(): Board = board

    fun getLevel(): PuzzleLevel? = currentLevel

    fun getMoveCount(): Int = moveCount

    fun getMistakeCount(): Int = mistakeCount

    fun getUndoStack(): List<Arrow> = undoStack.toList()

    fun reset() {
        currentLevel?.let { loadLevel(it) }
    }
}
