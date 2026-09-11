package com.navloka.arrowrush

import androidx.compose.ui.unit.IntOffset
import com.navloka.arrowrush.ui.components.boardCellOffset
import org.junit.Assert.assertEquals
import org.junit.Test

class BoardLayoutTest {

    @Test
    fun `level 1 arrows map from the board top left without a center shift`() {
        val cellSize = 100f

        assertEquals(IntOffset(200, 100), boardCellOffset(1, 2, cellSize, cellSize))
        assertEquals(IntOffset(100, 100), boardCellOffset(1, 1, cellSize, cellSize))
        assertEquals(IntOffset(0, 100), boardCellOffset(1, 0, cellSize, cellSize))
    }
}
