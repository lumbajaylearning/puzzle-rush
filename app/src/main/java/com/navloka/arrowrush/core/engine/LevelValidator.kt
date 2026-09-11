package com.navloka.arrowrush.core.engine

import com.navloka.arrowrush.core.model.PuzzleLevel

data class LevelValidationReport(
    val isValid: Boolean,
    val levelNumber: Int,
    val startStateValid: Boolean,
    val targetStateReached: Boolean,
    val movementRulesSatisfied: Boolean,
    val initialArrowCount: Int,
    val remainingArrowCount: Int,
    val solutionSequence: List<Int>,
    val errorMessage: String? = null
)

class LevelValidator(private val engine: PuzzleEngine = PuzzleEngine()) {

    fun validateLevel(level: PuzzleLevel): LevelValidationReport {
        // 1. Validate grid bounds
        if (level.rows <= 0 || level.cols <= 0) {
            return LevelValidationReport(
                isValid = false,
                levelNumber = level.levelNumber,
                startStateValid = false,
                targetStateReached = false,
                movementRulesSatisfied = false,
                initialArrowCount = level.arrows.size,
                remainingArrowCount = level.arrows.size,
                solutionSequence = emptyList(),
                errorMessage = "Invalid board dimensions: ${level.rows}x${level.cols}"
            )
        }

        // 2. Validate arrow count
        if (level.arrows.isEmpty()) {
            return LevelValidationReport(
                isValid = false,
                levelNumber = level.levelNumber,
                startStateValid = false,
                targetStateReached = false,
                movementRulesSatisfied = false,
                initialArrowCount = 0,
                remainingArrowCount = 0,
                solutionSequence = emptyList(),
                errorMessage = "Level contains no arrows"
            )
        }

        // 3. Validate unique arrow IDs
        val ids = level.arrows.map { it.id }
        if (ids.size != ids.distinct().size) {
            val duplicates = ids.groupBy { it }.filter { it.value.size > 1 }.keys
            return LevelValidationReport(
                isValid = false,
                levelNumber = level.levelNumber,
                startStateValid = false,
                targetStateReached = false,
                movementRulesSatisfied = false,
                initialArrowCount = level.arrows.size,
                remainingArrowCount = level.arrows.size,
                solutionSequence = emptyList(),
                errorMessage = "Duplicate arrow IDs found: $duplicates"
            )
        }

        // 4. Validate every occupied path cell is within grid bounds
        for (arrow in level.arrows) {
            val outOfBounds = arrow.path.firstOrNull { position ->
                position.row !in 0 until level.rows || position.col !in 0 until level.cols
            }
            if (outOfBounds != null) {
                return LevelValidationReport(
                    isValid = false,
                    levelNumber = level.levelNumber,
                    startStateValid = false,
                    targetStateReached = false,
                    movementRulesSatisfied = false,
                    initialArrowCount = level.arrows.size,
                    remainingArrowCount = level.arrows.size,
                    solutionSequence = emptyList(),
                    errorMessage = "Arrow ${arrow.id} cell $outOfBounds is out of bounds (${level.rows}x${level.cols})"
                )
            }
        }

        // 5. Validate unique occupied cells (paths cannot overlap)
        val positions = level.arrows.flatMap { it.path }
        if (positions.size != positions.distinct().size) {
            val duplicates = positions.groupBy { it }.filter { it.value.size > 1 }.keys
            return LevelValidationReport(
                isValid = false,
                levelNumber = level.levelNumber,
                startStateValid = false,
                targetStateReached = false,
                movementRulesSatisfied = false,
                initialArrowCount = level.arrows.size,
                remainingArrowCount = level.arrows.size,
                solutionSequence = emptyList(),
                errorMessage = "Overlapping arrows at same start position found: $duplicates"
            )
        }

        // 6. Test movement rules and solve towards target state using an isolated engine
        val localEngine = PuzzleEngine(level)
        val solution = mutableListOf<Int>()
        var movementRulesSatisfied = true

        while (!localEngine.isSolved()) {
            val available = localEngine.getAvailableMoves()
            if (available.isEmpty()) {
                return LevelValidationReport(
                    isValid = false,
                    levelNumber = level.levelNumber,
                    startStateValid = true,
                    targetStateReached = false,
                    movementRulesSatisfied = movementRulesSatisfied,
                    initialArrowCount = level.arrows.size,
                    remainingArrowCount = localEngine.getRemainingArrowCount(),
                    solutionSequence = solution,
                    errorMessage = "Deadlock encountered with ${localEngine.getRemainingArrowCount()} arrows remaining"
                )
            }

            // Verify movement rules: arrows in 'available' can escape; arrows not in 'available' are blocked
            val currentBoard = localEngine.getBoardState()
            for (arrow in currentBoard.arrows.values) {
                val canEscape = CollisionRules.canEscape(arrow, currentBoard)
                if (arrow.id in available && !canEscape) {
                    movementRulesSatisfied = false
                }
                if (arrow.id !in available && canEscape) {
                    movementRulesSatisfied = false
                }
            }

            val nextMove = available.first()
            val result = localEngine.move(nextMove)
            if (result is MoveResult.Success) {
                solution.add(nextMove)
            } else {
                return LevelValidationReport(
                    isValid = false,
                    levelNumber = level.levelNumber,
                    startStateValid = true,
                    targetStateReached = false,
                    movementRulesSatisfied = false,
                    initialArrowCount = level.arrows.size,
                    remainingArrowCount = localEngine.getRemainingArrowCount(),
                    solutionSequence = solution,
                    errorMessage = "Move failed for arrow $nextMove: $result"
                )
            }
        }

        val targetStateReached = localEngine.isSolved() &&
            localEngine.getRemainingArrowCount() == 0 &&
            solution.size == level.arrows.size

        return LevelValidationReport(
            isValid = targetStateReached && movementRulesSatisfied,
            levelNumber = level.levelNumber,
            startStateValid = true,
            targetStateReached = targetStateReached,
            movementRulesSatisfied = movementRulesSatisfied,
            initialArrowCount = level.arrows.size,
            remainingArrowCount = localEngine.getRemainingArrowCount(),
            solutionSequence = solution,
            errorMessage = if (!targetStateReached) "Target cleared state not reached" else null
        )
    }

    fun isSolvable(level: PuzzleLevel): Boolean {
        return validateLevel(level).isValid
    }

    fun minimumMoves(level: PuzzleLevel): Int {
        val report = validateLevel(level)
        return if (report.isValid) report.solutionSequence.size else -1
    }

    fun solutionSequence(level: PuzzleLevel): List<Int>? {
        val report = validateLevel(level)
        return if (report.isValid) report.solutionSequence else null
    }

    companion object {
        private val defaultValidator = LevelValidator()

        fun validate(level: PuzzleLevel): LevelValidationReport = defaultValidator.validateLevel(level)

        fun isSolvable(level: PuzzleLevel): Boolean = defaultValidator.isSolvable(level)

        fun solutionSequence(level: PuzzleLevel): List<Int>? = defaultValidator.solutionSequence(level)

        fun minimumMoves(level: PuzzleLevel): Int = defaultValidator.minimumMoves(level)
    }
}
