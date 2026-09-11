package com.navloka.arrowrush

import androidx.compose.ui.unit.IntOffset
import com.navloka.arrowrush.ui.components.boardCellOffset
import org.junit.Assert.assertEquals
import org.junit.Test

class BoardLayoutTest {

    @Test
    fun `level 1 path heads map from the board top left without a center shift`() {
        val cellSize = 100f

        assertEquals(IntOffset(200, 0), boardCellOffset(0, 2, cellSize, cellSize))
        assertEquals(IntOffset(200, 200), boardCellOffset(2, 2, cellSize, cellSize))
        assertEquals(IntOffset(200, 400), boardCellOffset(4, 2, cellSize, cellSize))
    }
}
