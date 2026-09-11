package com.navloka.arrowrush

import com.navloka.arrowrush.core.engine.LevelValidator
import com.navloka.arrowrush.core.scoring.FocusRules
import com.navloka.arrowrush.data.LevelCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CampaignDesignTest {

    @Test
    fun `campaign uses fewer multi-cell paths while every dependency order stays valid`() {
        val puzzleLevels = LevelCatalog.levels.drop(1)

        puzzleLevels.forEach { level ->
            assertTrue("Level ${level.levelNumber} should avoid excessive tapping", level.arrows.size <= 14)
            assertTrue(
                "Level ${level.levelNumber} should use path-shaped pieces",
                level.arrows.all { it.path.size >= 2 }
            )
            assertEquals(
                "Level ${level.levelNumber} must be solvable in its authored dependency order",
                (1..level.arrows.size).toList(),
                LevelValidator.solutionSequence(level)
            )
        }

        assertTrue(
            "Late campaign boards should add decisions without returning to tap-heavy levels",
            LevelCatalog.getLevel(30).arrows.size > LevelCatalog.getLevel(5).arrows.size
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
