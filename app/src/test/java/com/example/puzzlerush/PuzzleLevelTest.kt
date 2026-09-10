package com.example.puzzlerush

import com.example.puzzlerush.data.LevelCatalog
import com.example.puzzlerush.model.ArrowBlock
import com.example.puzzlerush.model.PuzzleLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleLevelTest {

    private fun isSolvable(level: PuzzleLevel): Boolean {
        val remaining = level.arrows.toMutableList()
        while (remaining.isNotEmpty()) {
            val activePositions = remaining.map { Pair(it.row, it.col) }.toSet()
            val unblocked = remaining.firstOrNull { arrow ->
                LevelCatalog.isArrowUnblocked(arrow, activePositions, level.rows, level.cols)
            } ?: return false
            remaining.remove(unblocked)
        }
        return true
    }

    @Test
    fun testExactly30LevelsExist() {
        assertEquals("There should be exactly 30 levels for Milestone 1", 30, LevelCatalog.levels.size)
        for (i in 1..30) {
            val lvl = LevelCatalog.getLevel(i)
            assertEquals("Level numbers should match sequence", i, lvl.levelNumber)
            assertTrue("Level $i should have at least 3 arrows", lvl.arrows.size >= 3)
        }
    }

    @Test
    fun testAll30LevelsAreSolvable() {
        val unsolvableLevels = mutableListOf<String>()
        for (level in LevelCatalog.levels) {
            val solvable = isSolvable(level)
            if (!solvable) {
                unsolvableLevels.add("Level ${level.levelNumber} (${level.title})")
            }
        }
        assertTrue(
            "The following levels are unsolvable: $unsolvableLevels",
            unsolvableLevels.isEmpty()
        )
    }

    @Test
    fun testBlockerDetectionLogic() {
        // In Level 1: arrows at (1,2 RIGHT), (1,1 RIGHT), (1,0 RIGHT)
        // Arrow 3 at (1,0) pointing RIGHT should be blocked by Arrow 2 at (1,1)
        val level1 = LevelCatalog.getLevel(1)
        val arrowMap = level1.arrows.associateBy { it.id }
        val arrow3 = arrowMap[3]!!
        val blocker = LevelCatalog.findFirstBlocker(arrow3, arrowMap, level1.rows, level1.cols)
        org.junit.Assert.assertNotNull("Arrow 3 should have a blocker", blocker)
        assertEquals("Arrow 3 blocker should be Arrow 2", 2, blocker?.id)

        // Arrow 1 at (1,2) pointing RIGHT has free path to col 3 (off board)
        val arrow1 = arrowMap[1]!!
        val blocker1 = LevelCatalog.findFirstBlocker(arrow1, arrowMap, level1.rows, level1.cols)
        org.junit.Assert.assertNull("Arrow 1 should have no blocker", blocker1)
    }

    @Test
    fun testDirectionDeltas() {
        assertEquals(-1, com.example.puzzlerush.model.Direction.UP.dRow)
        assertEquals(0, com.example.puzzlerush.model.Direction.UP.dCol)

        assertEquals(1, com.example.puzzlerush.model.Direction.DOWN.dRow)
        assertEquals(0, com.example.puzzlerush.model.Direction.DOWN.dCol)

        assertEquals(0, com.example.puzzlerush.model.Direction.LEFT.dRow)
        assertEquals(-1, com.example.puzzlerush.model.Direction.LEFT.dCol)

        assertEquals(0, com.example.puzzlerush.model.Direction.RIGHT.dRow)
        assertEquals(1, com.example.puzzlerush.model.Direction.RIGHT.dCol)
    }
}
