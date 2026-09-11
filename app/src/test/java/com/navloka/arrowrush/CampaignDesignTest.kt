package com.navloka.arrowrush

import com.navloka.arrowrush.core.engine.LevelValidator
import com.navloka.arrowrush.core.scoring.FocusRules
import com.navloka.arrowrush.data.LevelCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CampaignDesignTest {

    @Test
    fun `campaign becomes denser while every intended dependency order stays valid`() {
        val puzzleLevels = LevelCatalog.levels.drop(1)

        puzzleLevels.forEach { level ->
            val density = level.arrows.size.toFloat() / (level.rows * level.cols)
            assertTrue("Level ${level.levelNumber} should feel visually dense", density >= 0.68f)
            assertEquals(
                "Level ${level.levelNumber} must be solvable in its authored dependency order",
                (1..level.arrows.size).toList(),
                LevelValidator.solutionSequence(level)
            )
        }

        assertTrue(
            "Late campaign boards should contain more arrows than early boards",
            LevelCatalog.getLevel(30).arrows.size > LevelCatalog.getLevel(5).arrows.size * 3
        )
    }

    @Test
    fun `three blocked campaign moves deplete focus without going negative`() {
        var focus = FocusRules.MAX_FOCUS
        repeat(3) { focus = FocusRules.afterBlockedMove(focus) }

        assertEquals(0, focus)
        assertTrue(FocusRules.isDepleted(focus))
        assertEquals(0, FocusRules.afterBlockedMove(focus))
    }
}
