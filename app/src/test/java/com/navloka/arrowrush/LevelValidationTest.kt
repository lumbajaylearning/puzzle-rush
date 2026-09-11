package com.navloka.arrowrush

import com.navloka.arrowrush.core.engine.CollisionRules
import com.navloka.arrowrush.core.engine.LevelValidator
import com.navloka.arrowrush.core.engine.MoveResult
import com.navloka.arrowrush.core.engine.MovementRules
import com.navloka.arrowrush.core.engine.PuzzleEngine
import com.navloka.arrowrush.core.engine.WinDetector
import com.navloka.arrowrush.core.generator.DifficultyEvaluator
import com.navloka.arrowrush.core.generator.LevelGenerator
import com.navloka.arrowrush.core.model.Direction
import com.navloka.arrowrush.core.model.Difficulty
import com.navloka.arrowrush.core.model.Position
import com.navloka.arrowrush.core.scoring.ScoreCalculator
import com.navloka.arrowrush.data.LevelCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LevelValidationTest {

    @Test
    fun `Level 1 LevelValidator confirms start state, movement rules, and target state`() {
        val level1 = LevelCatalog.getLevel(1)

        // 1. START STATE VALIDATION
        assertEquals("Level 1 number should be 1", 1, level1.levelNumber)
        assertEquals("Level 1 title is 'First Flight'", "First Flight", level1.title)
        assertEquals("Level 1 grid rows should be 7", 7, level1.rows)
        assertEquals("Level 1 grid cols should be 5", 5, level1.cols)
        assertEquals("Level 1 should start with exactly 3 paths", 3, level1.arrows.size)

        // Path 1 is the front piece nearest the top edge.
        val arrow1 = level1.arrows.find { it.id == 1 }
        assertNotNull("Arrow 1 must exist", arrow1)
        assertEquals(Position(0, 2), arrow1!!.position)
        assertEquals(Direction.UP, arrow1.direction)
        assertEquals(listOf(Position(1, 2), Position(0, 2)), arrow1.path)

        // Path 2 is blocked by Path 1 until it escapes.
        val arrow2 = level1.arrows.find { it.id == 2 }
        assertNotNull("Arrow 2 must exist", arrow2)
        assertEquals(Position(2, 2), arrow2!!.position)
        assertEquals(Direction.UP, arrow2.direction)
        assertEquals(listOf(Position(3, 2), Position(2, 2)), arrow2.path)

        // Path 3 is blocked by Path 2.
        val arrow3 = level1.arrows.find { it.id == 3 }
        assertNotNull("Arrow 3 must exist", arrow3)
        assertEquals(Position(4, 2), arrow3!!.position)
        assertEquals(Direction.UP, arrow3.direction)
        assertEquals(listOf(Position(5, 2), Position(4, 2)), arrow3.path)

        // 2. LEVEL VALIDATOR REPORT FOR LEVEL 1
        val report = LevelValidator.validate(level1)
        assertTrue("LevelValidator report must report level 1 is valid", report.isValid)
        assertTrue("LevelValidator report must verify start state is valid", report.startStateValid)
        assertTrue("LevelValidator report must verify movement rules are satisfied", report.movementRulesSatisfied)
        assertTrue("LevelValidator report must verify target state is reached", report.targetStateReached)
        assertEquals("Initial arrow count must be 3", 3, report.initialArrowCount)
        assertEquals("Remaining arrow count at target must be 0", 0, report.remainingArrowCount)
        assertEquals("Solution sequence must be [1, 2, 3]", listOf(1, 2, 3), report.solutionSequence)
        assertNull("Error message should be null for valid level", report.errorMessage)

        // 3. STEP-BY-STEP MOVEMENT RULES & STATE TRANSITIONS
        val engine = PuzzleEngine(level1)
        assertFalse("Engine should not be solved at start state", engine.isSolved())
        assertEquals(3, engine.getRemainingArrowCount())

        // In start state, ONLY Arrow 1 has a clear path out of the board
        val initialAvailable = engine.getAvailableMoves()
        assertEquals("Only Arrow 1 should be available in start state", listOf(1), initialAvailable)
        assertTrue("Arrow 1 can escape through the top edge", MovementRules.canMove(1, engine.getBoardState()))
        assertNull("Arrow 1 has no blocking arrow", MovementRules.getBlockingArrow(1, engine.getBoardState()))

        assertFalse("Arrow 2 cannot escape in start state", MovementRules.canMove(2, engine.getBoardState()))
        assertEquals("Arrow 2 blocker is Arrow 1", 1, MovementRules.getBlockingArrow(2, engine.getBoardState())?.id)

        assertFalse("Arrow 3 cannot escape in start state", MovementRules.canMove(3, engine.getBoardState()))
        assertEquals("Arrow 3 blocker is Arrow 2", 2, MovementRules.getBlockingArrow(3, engine.getBoardState())?.id)

        // Attempting blocked moves must fail and preserve start state
        val blockedMove2 = engine.move(2)
        assertTrue("Moving Arrow 2 first must be Blocked", blockedMove2 is MoveResult.Blocked)
        assertEquals(1, (blockedMove2 as MoveResult.Blocked).blockingArrow.id)
        assertEquals(3, engine.getRemainingArrowCount())

        val blockedMove3 = engine.move(3)
        assertTrue("Moving Arrow 3 first must be Blocked", blockedMove3 is MoveResult.Blocked)
        assertEquals(2, (blockedMove3 as MoveResult.Blocked).blockingArrow.id)
        assertEquals(3, engine.getRemainingArrowCount())

        // Step 1: Move Arrow 1 -> escapes
        val move1 = engine.move(1)
        assertTrue("Moving Arrow 1 must succeed", move1 is MoveResult.Success)
        assertFalse("Level is not yet solved after 1 move", (move1 as MoveResult.Success).isSolved)
        assertEquals(2, engine.getRemainingArrowCount())
        assertEquals("Only Arrow 2 should now be available", listOf(2), engine.getAvailableMoves())
        assertTrue("Arrow 2 can now escape", MovementRules.canMove(2, engine.getBoardState()))
        assertNull("Arrow 2 has no blocker now", MovementRules.getBlockingArrow(2, engine.getBoardState()))
        assertFalse("Arrow 3 is still blocked", MovementRules.canMove(3, engine.getBoardState()))
        assertEquals("Arrow 3 blocker is Arrow 2", 2, MovementRules.getBlockingArrow(3, engine.getBoardState())?.id)

        // Step 2: Move Arrow 2 -> escapes
        val move2 = engine.move(2)
        assertTrue("Moving Arrow 2 must succeed", move2 is MoveResult.Success)
        assertFalse("Level is not yet solved after 2 moves", (move2 as MoveResult.Success).isSolved)
        assertEquals(1, engine.getRemainingArrowCount())
        assertEquals("Only Arrow 3 should now be available", listOf(3), engine.getAvailableMoves())
        assertTrue("Arrow 3 can now escape", MovementRules.canMove(3, engine.getBoardState()))
        assertNull("Arrow 3 has no blocker now", MovementRules.getBlockingArrow(3, engine.getBoardState()))

        // Step 3: Move Arrow 3 -> escapes to reach TARGET STATE
        val move3 = engine.move(3)
        assertTrue("Moving Arrow 3 must succeed", move3 is MoveResult.Success)
        assertTrue("Level 1 must be solved after clearing all 3 arrows", (move3 as MoveResult.Success).isSolved)

        // 4. TARGET STATE VALIDATION
        assertTrue("Engine must report isSolved() == true", engine.isSolved())
        assertEquals("Target state remaining arrows count must be 0", 0, engine.getRemainingArrowCount())
        assertTrue("Board must have no remaining arrows", engine.getBoardState().arrows.isEmpty())
        assertTrue("WinDetector must detect board is solved", WinDetector.isSolved(engine.getBoardState()))
        assertEquals("LevelValidator minimum moves must be 3", 3, LevelValidator.minimumMoves(level1))
        assertEquals("LevelValidator solution sequence must be [1, 2, 3]", listOf(1, 2, 3), LevelValidator.solutionSequence(level1))
    }

    @Test
    fun `exactly 30 campaign levels exist`() {
        assertEquals(30, LevelCatalog.levels.size)
        for (i in 1..30) {
            val level = LevelCatalog.getLevel(i)
            assertEquals("Level numbers must be sequential 1 to 30", i, level.levelNumber)
            assertTrue("Level $i must contain arrows", level.arrows.isNotEmpty())
        }
    }

    @Test
    fun `all 30 campaign levels are guaranteed solvable`() {
        for (level in LevelCatalog.levels) {
            val isSolvable = LevelValidator.isSolvable(level)
            assertTrue(
                "Level ${level.levelNumber} ('${level.title}') MUST be solvable!",
                isSolvable
            )

            val solution = LevelValidator.solutionSequence(level)
            assertNotNull("Solution sequence for level ${level.levelNumber} must exist", solution)
            assertEquals(
                "Solution must clear all arrows in level ${level.levelNumber}",
                level.arrows.size,
                solution?.size
            )

            // Verify solution by executing through PuzzleEngine
            val engine = PuzzleEngine(level)
            solution?.forEach { arrowId: Int ->
                val result = engine.move(arrowId)
                assertTrue(
                    "Move $arrowId in solution for level ${level.levelNumber} failed: $result",
                    result is com.navloka.arrowrush.core.engine.MoveResult.Success
                )
            }
            assertTrue("Level ${level.levelNumber} must be completely solved", engine.isSolved())
        }
    }

    @Test
    fun `score calculator assigns correct star ratings`() {
        // Optimal moves, 0 mistakes -> 3 stars
        assertEquals(3, ScoreCalculator.calculateStars(moves = 5, mistakes = 0, optimalMoves = 5))

        // Extra moves but 0 mistakes -> 2 stars
        assertEquals(2, ScoreCalculator.calculateStars(moves = 6, mistakes = 0, optimalMoves = 5))

        // Optimal moves with 1 mistake -> 2 stars
        assertEquals(2, ScoreCalculator.calculateStars(moves = 5, mistakes = 1, optimalMoves = 5))

        // More than 2 mistakes -> 1 star
        assertEquals(1, ScoreCalculator.calculateStars(moves = 5, mistakes = 3, optimalMoves = 5))
    }

    @Test
    fun `difficulty evaluator rates levels appropriately`() {
        val lvl1 = LevelCatalog.getLevel(1)
        val lvl30 = LevelCatalog.getLevel(30)

        val eval1 = DifficultyEvaluator.calculateMetrics(lvl1)
        val eval30 = DifficultyEvaluator.calculateMetrics(lvl30)

        assertTrue(
            "Level 30 difficulty score (${eval30.difficultyScore}) must be higher than Level 1 (${eval1.difficultyScore})",
            eval30.difficultyScore > eval1.difficultyScore
        )
    }

    @Test
    fun `level generator generates solvable levels backwards`() {
        val generator = LevelGenerator()
        for (difficulty in listOf(Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD)) {
            val generated = generator.generate(
                levelNumber = 99,
                difficulty = difficulty
            )
            assertTrue("Generated level must have arrows", generated.arrows.isNotEmpty())
            assertTrue("Generated level must be solvable by LevelValidator", LevelValidator.isSolvable(generated))
        }
    }
}
