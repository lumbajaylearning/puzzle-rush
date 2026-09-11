package com.navloka.arrowrush

import com.navloka.arrowrush.core.engine.MovementFeedbackListener
import com.navloka.arrowrush.core.engine.MoveResult
import com.navloka.arrowrush.core.engine.PuzzleEngine
import com.navloka.arrowrush.core.model.Arrow
import com.navloka.arrowrush.core.model.Direction
import com.navloka.arrowrush.core.model.Position
import com.navloka.arrowrush.core.model.PuzzleLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleEngineTest {

    @Test
    fun `unblocked arrow leaves board and decreases remaining count`() {
        // Arrow pointing right with clear path to col edge
        val arrow1 = Arrow(id = 1, position = Position(0, 0), direction = Direction.RIGHT)
        val level = PuzzleLevel(
            levelNumber = 1,
            title = "Test Level",
            rows = 2,
            cols = 2,
            arrows = listOf(arrow1)
        )

        val engine = PuzzleEngine(level)
        assertEquals(1, engine.getRemainingArrowCount())
        assertTrue(engine.canMove(1))

        val result = engine.move(1)
        assertTrue(result is MoveResult.Success)
        assertEquals(0, engine.getRemainingArrowCount())
        assertTrue(engine.isSolved())
    }

    @Test
    fun `blocked arrow cannot move and identifies blocker`() {
        // Arrow 1 at (0,0) pointing RIGHT is blocked by Arrow 2 at (0,1)
        val arrow1 = Arrow(id = 1, position = Position(0, 0), direction = Direction.RIGHT)
        val arrow2 = Arrow(id = 2, position = Position(0, 1), direction = Direction.UP)
        val level = PuzzleLevel(
            levelNumber = 1,
            title = "Blocker Test",
            rows = 2,
            cols = 2,
            arrows = listOf(arrow1, arrow2)
        )

        val engine = PuzzleEngine(level)
        assertFalse(engine.canMove(1))
        assertTrue(engine.canMove(2)) // Arrow 2 points UP and has clear path

        val blockedResult = engine.move(1)
        assertTrue(blockedResult is MoveResult.Blocked)
        val blocker = (blockedResult as MoveResult.Blocked).blockingArrow
        assertEquals(2, blocker.id)

        // Board state unchanged after failed move
        assertEquals(2, engine.getRemainingArrowCount())
    }

    @Test
    fun `clearing blocker unblocks subsequent arrow`() {
        val arrow1 = Arrow(id = 1, position = Position(0, 0), direction = Direction.RIGHT)
        val arrow2 = Arrow(id = 2, position = Position(0, 1), direction = Direction.UP)
        val level = PuzzleLevel(
            levelNumber = 1,
            title = "Untangle Test",
            rows = 2,
            cols = 2,
            arrows = listOf(arrow1, arrow2)
        )

        val engine = PuzzleEngine(level)

        // Clear blocker first
        val move2 = engine.move(2)
        assertTrue(move2 is MoveResult.Success)
        assertEquals(1, engine.getRemainingArrowCount())

        // Now arrow 1 is unblocked
        assertTrue(engine.canMove(1))
        val move1 = engine.move(1)
        assertTrue(move1 is MoveResult.Success)
        assertTrue((move1 as MoveResult.Success).isSolved)
        assertTrue(engine.isSolved())
    }

    @Test
    fun `undo restores previous state accurately`() {
        val arrow1 = Arrow(id = 1, position = Position(0, 0), direction = Direction.UP)
        val arrow2 = Arrow(id = 2, position = Position(1, 1), direction = Direction.DOWN)
        val level = PuzzleLevel(
            levelNumber = 1,
            title = "Undo Test",
            rows = 2,
            cols = 2,
            arrows = listOf(arrow1, arrow2)
        )

        val engine = PuzzleEngine(level)
        assertFalse(engine.canUndo())

        engine.move(1)
        assertEquals(1, engine.getRemainingArrowCount())
        assertTrue(engine.canUndo())

        val undone = engine.undo()
        assertNotNull(undone)
        assertEquals(1, undone?.id)
        assertEquals(2, engine.getRemainingArrowCount())
        assertFalse(engine.canUndo())
    }

    @Test
    fun `reset restores initial level board`() {
        val arrow1 = Arrow(id = 1, position = Position(0, 0), direction = Direction.UP)
        val arrow2 = Arrow(id = 2, position = Position(1, 1), direction = Direction.DOWN)
        val level = PuzzleLevel(
            levelNumber = 1,
            title = "Reset Test",
            rows = 2,
            cols = 2,
            arrows = listOf(arrow1, arrow2)
        )

        val engine = PuzzleEngine(level)
        engine.move(1)
        engine.move(2)
        assertTrue(engine.isSolved())

        engine.reset()
        assertEquals(2, engine.getRemainingArrowCount())
        assertFalse(engine.isSolved())
        assertFalse(engine.canUndo())
    }

    @Test
    fun `available moves returns all valid unblocked arrows`() {
        val arrow1 = Arrow(id = 1, position = Position(0, 0), direction = Direction.RIGHT) // blocked by arrow 2
        val arrow2 = Arrow(id = 2, position = Position(0, 1), direction = Direction.UP) // free
        val arrow3 = Arrow(id = 3, position = Position(1, 0), direction = Direction.DOWN) // free
        val level = PuzzleLevel(
            levelNumber = 1,
            title = "Available Moves",
            rows = 2,
            cols = 2,
            arrows = listOf(arrow1, arrow2, arrow3)
        )

        val engine = PuzzleEngine(level)
        val available = engine.getAvailableMoves()
        assertEquals(2, available.size)
        assertTrue(available.any { it == 2 })
        assertTrue(available.any { it == 3 })
        assertFalse(available.any { it == 1 })
    }

    @Test
    fun `PuzzleGameViewModel has public single-argument Application constructor for AndroidViewModelFactory`() {
        val constructor = com.navloka.arrowrush.viewmodel.PuzzleGameViewModel::class.java.getConstructor(android.app.Application::class.java)
        assertNotNull(constructor)
    }

    @Test
    fun `all 30 levels in LevelCatalog are solvable without deadlock`() {
        for (lvl in com.navloka.arrowrush.data.LevelCatalog.levels) {
            val engine = PuzzleEngine(lvl)
            var moves = 0
            while (!engine.isSolved() && moves < 200) {
                val available = engine.getAvailableMoves()
                assertTrue("Level ${lvl.levelNumber}: '${lvl.title}' has no valid moves left (deadlock)!", available.isNotEmpty())
                val res = engine.move(available.first())
                assertTrue("Level ${lvl.levelNumber} move failed: $res", res is MoveResult.Success)
                moves++
            }
            assertTrue("Level ${lvl.levelNumber} was not solved within move limit!", engine.isSolved())
        }
    }

    @Test
    fun `strictly enforces trace along direction rule over multiple cells and triggers feedback interface`() {
        // 4x4 grid: Arrow 1 at (0, 0) pointing RIGHT.
        // Arrow 2 is 3 cells away at (0, 3) pointing DOWN.
        val arrow1 = Arrow(id = 1, position = Position(0, 0), direction = Direction.RIGHT)
        val arrow2 = Arrow(id = 2, position = Position(0, 3), direction = Direction.DOWN)
        val level = PuzzleLevel(
            levelNumber = 99,
            title = "Distance Ray Trace Test",
            rows = 4,
            cols = 4,
            arrows = listOf(arrow1, arrow2)
        )

        var blockedCallbackInvoked = false
        var blockedArrowId = -1
        var blockerArrowId = -1
        var successCallbackInvoked = false
        var successArrowId = -1

        val engine = PuzzleEngine(level)
        engine.feedbackListener = object : MovementFeedbackListener {
            override fun onMoveBlocked(arrow: Arrow, blockingArrow: Arrow) {
                blockedCallbackInvoked = true
                blockedArrowId = arrow.id
                blockerArrowId = blockingArrow.id
            }

            override fun onMoveSuccess(arrow: Arrow, remainingCount: Int, isSolved: Boolean) {
                successCallbackInvoked = true
                successArrowId = arrow.id
            }
        }

        // 1. Moving Arrow 1 must trace along RIGHT direction and hit Arrow 2 at (0, 3)
        val moveResult1 = engine.move(1)
        assertTrue(moveResult1 is MoveResult.Blocked)
        assertEquals(2, (moveResult1 as MoveResult.Blocked).blockingArrow.id)
        assertTrue("Feedback interface onMoveBlocked must be triggered", blockedCallbackInvoked)
        assertEquals(1, blockedArrowId)
        assertEquals(2, blockerArrowId)
        assertEquals(2, engine.getRemainingArrowCount())
        assertEquals(1, engine.getMistakeCount())

        // 2. Moving Arrow 2 (points DOWN) has clear path to (3, 3) and off board
        val moveResult2 = engine.move(2)
        assertTrue(moveResult2 is MoveResult.Success)
        assertTrue("Feedback interface onMoveSuccess must be triggered", successCallbackInvoked)
        assertEquals(2, successArrowId)
        assertEquals(1, engine.getRemainingArrowCount())

        // 3. Arrow 1 now has clear path out of board
        blockedCallbackInvoked = false
        successCallbackInvoked = false
        val moveResult1Again = engine.move(1)
        assertTrue(moveResult1Again is MoveResult.Success)
        assertTrue("Arrow 1 can now escape off-board", successCallbackInvoked)
        assertEquals(1, successArrowId)
        assertEquals(0, engine.getRemainingArrowCount())
        assertTrue(engine.isSolved())
    }
}
