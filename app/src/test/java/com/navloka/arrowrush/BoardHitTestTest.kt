package com.navloka.arrowrush

import androidx.compose.ui.geometry.Offset
import com.navloka.arrowrush.core.model.Arrow
import com.navloka.arrowrush.core.model.Direction
import com.navloka.arrowrush.ui.components.findArrowAtBoardPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BoardHitTestTest {

    private val arrows = listOf(
        Arrow(1, 0, 0, Direction.RIGHT),
        Arrow(2, 0, 1, Direction.DOWN),
        Arrow(3, 1, 0, Direction.UP),
        Arrow(4, 1, 1, Direction.LEFT)
    ).associateBy { it.id }

    @Test
    fun `each board cell always resolves to its own arrow`() {
        repeat(20) {
            assertEquals(1, arrowAt(Offset(25f, 25f)))
            assertEquals(2, arrowAt(Offset(75f, 25f)))
            assertEquals(3, arrowAt(Offset(25f, 75f)))
            assertEquals(4, arrowAt(Offset(75f, 75f)))
        }
    }

    @Test
    fun `empty and outside positions do not select another arrow`() {
        val sparseBoard = mapOf(1 to arrows.getValue(1))

        assertNull(arrowAt(Offset(75f, 25f), sparseBoard))
        assertNull(arrowAt(Offset(-1f, 25f)))
        assertNull(arrowAt(Offset(100f, 25f)))
        assertNull(arrowAt(Offset(25f, 100f)))
    }

    private fun arrowAt(position: Offset, board: Map<Int, Arrow> = arrows): Int? =
        findArrowAtBoardPosition(
            position = position,
            activeArrows = board,
            rows = 2,
            cols = 2,
            boardWidth = 100f,
            boardHeight = 100f
        )
}
