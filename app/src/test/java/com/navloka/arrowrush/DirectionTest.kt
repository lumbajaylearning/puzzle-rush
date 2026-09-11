package com.navloka.arrowrush

import com.navloka.arrowrush.core.model.Direction
import org.junit.Assert.assertEquals
import org.junit.Test

class DirectionTest {

    @Test
    fun `visual rotations match movement vectors for a right-facing base arrow`() {
        assertDirection(Direction.UP, dRow = -1, dCol = 0, angleDegrees = -90f)
        assertDirection(Direction.RIGHT, dRow = 0, dCol = 1, angleDegrees = 0f)
        assertDirection(Direction.DOWN, dRow = 1, dCol = 0, angleDegrees = 90f)
        assertDirection(Direction.LEFT, dRow = 0, dCol = -1, angleDegrees = 180f)
    }

    private fun assertDirection(
        direction: Direction,
        dRow: Int,
        dCol: Int,
        angleDegrees: Float
    ) {
        assertEquals(dRow, direction.dRow)
        assertEquals(dCol, direction.dCol)
        assertEquals(angleDegrees, direction.angleDegrees, 0f)
    }
}
