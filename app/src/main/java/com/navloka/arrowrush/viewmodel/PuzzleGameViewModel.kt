package com.navloka.arrowrush.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.navloka.arrowrush.audio.HapticFeedbackManager
import com.navloka.arrowrush.audio.SoundSynthesizer
import com.navloka.arrowrush.core.engine.MovementFeedbackListener
import com.navloka.arrowrush.core.engine.MoveResult
import com.navloka.arrowrush.core.engine.PuzzleEngine
import com.navloka.arrowrush.core.model.Arrow
import com.navloka.arrowrush.core.scoring.ScoreCalculator
import com.navloka.arrowrush.data.GamePreferences
import com.navloka.arrowrush.data.LevelCatalog
import com.navloka.arrowrush.model.GameMode
import com.navloka.arrowrush.model.GameState
import com.navloka.arrowrush.ui.theme.AppThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PuzzleGameViewModel @JvmOverloads constructor(
    application: Application,
    private val engine: PuzzleEngine = PuzzleEngine()
) : AndroidViewModel(application) {

    val prefs = GamePreferences(application)
    val sound = SoundSynthesizer().apply { isEnabled = prefs.soundEnabled }
    val haptics = HapticFeedbackManager(application).apply { isEnabled = prefs.hapticsEnabled }

    private val _currentScreen = MutableStateFlow(Screen.MAIN_MENU)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _themeMode = MutableStateFlow(
        try {
            AppThemeMode.valueOf(prefs.currentTheme)
        } catch (_: Exception) {
            AppThemeMode.ZEN
        }
    )
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val initialLevel = LevelCatalog.getLevel(prefs.highestUnlockedLevel.coerceAtMost(30))

    private val _gameState = MutableStateFlow(
        GameState(
            level = initialLevel,
            board = initialLevel.createInitialBoard()
        )
    )
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var rushTimerJob: Job? = null
    private var blockerClearJob: Job? = null
    private var hintClearJob: Job? = null
    private var moveAnimationJob: Job? = null

    init {
        engine.feedbackListener = object : MovementFeedbackListener {
            override fun onMoveBlocked(arrow: Arrow, blockingArrow: Arrow) {
                handleMoveBlocked(arrow, blockingArrow)
            }

            override fun onMoveSuccess(arrow: Arrow, remainingCount: Int, isSolved: Boolean) {
                handleMoveSuccess(arrow, remainingCount, isSolved)
            }
        }
        engine.loadLevel(initialLevel)
    }

    fun navigateTo(screen: Screen) {
        sound.playTap()
        haptics.vibrateClick()
        _currentScreen.value = screen
    }

    fun setTheme(theme: AppThemeMode) {
        _themeMode.value = theme
        prefs.currentTheme = theme.name
        sound.playTap()
        haptics.vibrateClick()
    }

    fun toggleSound() {
        val newVal = !prefs.soundEnabled
        prefs.soundEnabled = newVal
        sound.isEnabled = newVal
        if (newVal) sound.playTap()
    }

    fun toggleHaptics() {
        val newVal = !prefs.hapticsEnabled
        prefs.hapticsEnabled = newVal
        haptics.isEnabled = newVal
        if (newVal) haptics.vibrateClick()
    }

    fun startLevel(levelNumber: Int) {
        rushTimerJob?.cancel()
        hintClearJob?.cancel()
        blockerClearJob?.cancel()
        moveAnimationJob?.cancel()
        val lvl = LevelCatalog.getLevel(levelNumber)
        engine.loadLevel(lvl)

        val initialFeedback = if (levelNumber == 1) {
            "👉 Tap the front arrow on the right edge to launch it off the board!"
        } else null

        _gameState.value = GameState(
            level = lvl,
            mode = GameMode.CAMPAIGN,
            board = engine.getBoardState(),
            flyingArrowIds = emptySet(),
            blockedArrowId = null,
            blockerArrowId = null,
            hintArrowId = null,
            movesCount = 0,
            mistakesCount = 0,
            combo = 0,
            isCleared = false,
            starsEarned = 0,
            canUndo = false,
            feedbackMessage = initialFeedback
        )
        _currentScreen.value = Screen.GAMEPLAY
    }

    fun startRushMode() {
        rushTimerJob?.cancel()
        hintClearJob?.cancel()
        blockerClearJob?.cancel()
        moveAnimationJob?.cancel()
        val firstRushLvl = LevelCatalog.getLevel(1)
        engine.loadLevel(firstRushLvl)

        _gameState.value = GameState(
            level = firstRushLvl,
            mode = GameMode.RUSH,
            board = engine.getBoardState(),
            flyingArrowIds = emptySet(),
            blockedArrowId = null,
            blockerArrowId = null,
            hintArrowId = null,
            movesCount = 0,
            mistakesCount = 0,
            combo = 0,
            isCleared = false,
            starsEarned = 0,
            canUndo = false,
            rushTimeRemainingSeconds = 60,
            rushScore = 0,
            rushLevelsCleared = 0,
            isRushGameOver = false,
            feedbackMessage = "⚡ Rush: Tap open arrows quickly to build your combo!"
        )
        _currentScreen.value = Screen.GAMEPLAY
        startRushTimer()
    }

    private fun startRushTimer() {
        rushTimerJob?.cancel()
        rushTimerJob = viewModelScope.launch {
            while (_gameState.value.rushTimeRemainingSeconds > 0 && !_gameState.value.isRushGameOver) {
                delay(1000)
                if (_gameState.value.isRushGameOver) break

                val newTime = _gameState.value.rushTimeRemainingSeconds - 1
                if (newTime in 1..5) {
                    sound.playTick()
                }

                _gameState.update { it.copy(rushTimeRemainingSeconds = newTime) }

                if (newTime <= 0) {
                    endRushGame()
                    break
                }
            }
        }
    }

    private fun endRushGame() {
        rushTimerJob?.cancel()
        val finalState = _gameState.value
        val newHighScore = maxOf(finalState.rushScore, prefs.rushHighScore)
        val newMaxCombo = maxOf(finalState.combo, prefs.rushMaxCombo)
        val totalCleared = prefs.rushTotalArrowsCleared + finalState.movesCount

        prefs.rushHighScore = newHighScore
        prefs.rushMaxCombo = newMaxCombo
        prefs.rushTotalArrowsCleared = totalCleared

        _gameState.update {
            it.copy(
                isRushGameOver = true,
                rushTimeRemainingSeconds = 0
            )
        }
    }

    fun onArrowClicked(arrowId: Int) {
        val state = _gameState.value
        if (state.isCleared || state.isRushGameOver) return
        // The engine removes a successful arrow immediately while the UI keeps it for the
        // 220 ms fly-off animation. Lock the board during that tiny window so a second tap
        // cannot be evaluated against an engine state the player has not seen yet.
        if (state.flyingArrowIds.isNotEmpty()) return

        engine.move(arrowId)
    }

    private fun handleMoveBlocked(arrow: Arrow, blockingArrow: Arrow) {
        sound.playBump()
        haptics.vibrateBump()

        blockerClearJob?.cancel()
        val dirLabel = arrow.direction.label.uppercase()
        _gameState.update {
            it.copy(
                blockedArrowId = arrow.id,
                blockerArrowId = blockingArrow.id,
                mistakesCount = it.mistakesCount + 1,
                combo = 0,
                feedbackMessage = "🚫 BLOCKED: Moving $dirLabel is blocked by the highlighted arrow!"
            )
        }

        blockerClearJob = viewModelScope.launch {
            delay(2500)
            _gameState.update {
                if (it.blockedArrowId == arrow.id) {
                    it.copy(blockedArrowId = null, blockerArrowId = null, feedbackMessage = null)
                } else it
            }
        }
    }

    private fun handleMoveSuccess(arrow: Arrow, remainingCount: Int, isSolved: Boolean) {
        val state = _gameState.value
        val nextCombo = state.combo + 1
        val addedScore = if (state.mode == GameMode.RUSH) {
            ScoreCalculator.calculateRushMoveScore(nextCombo)
        } else 0

        if (state.mode == GameMode.RUSH) {
            sound.playCombo(nextCombo)
        } else {
            sound.playWhoosh()
        }
        haptics.vibrateClick()

        // Trigger off-board flying animation
        _gameState.update {
            it.copy(
                flyingArrowIds = it.flyingArrowIds + arrow.id,
                blockedArrowId = null,
                blockerArrowId = null,
                hintArrowId = if (it.hintArrowId == arrow.id) null else it.hintArrowId,
                feedbackMessage = null,
                movesCount = it.movesCount + 1,
                combo = nextCombo,
                rushScore = it.rushScore + addedScore,
                canUndo = engine.canUndo()
            )
        }

        moveAnimationJob?.cancel()
        moveAnimationJob = viewModelScope.launch {
            delay(220) // Duration of arrow fly-off
            val current = _gameState.value
            val updatedBoard = engine.getBoardState()

            if (isSolved) {
                // Level cleared!
                sound.playWin()
                haptics.vibrateSuccess()

                if (current.mode == GameMode.CAMPAIGN) {
                    val stars = ScoreCalculator.calculateStars(
                        moves = current.movesCount,
                        mistakes = current.mistakesCount,
                        optimalMoves = current.level.optimalMoves
                    )
                    prefs.setLevelStars(current.level.levelNumber, stars)
                    prefs.setBestMoves(current.level.levelNumber, current.movesCount)
                    if (current.level.levelNumber >= prefs.highestUnlockedLevel && current.level.levelNumber < 30) {
                        prefs.highestUnlockedLevel = current.level.levelNumber + 1
                    }

                    _gameState.update {
                        it.copy(
                            board = updatedBoard,
                            flyingArrowIds = it.flyingArrowIds - arrow.id,
                            isCleared = true,
                            starsEarned = stars,
                            canUndo = false
                        )
                    }
                } else {
                    // Rush Mode: cleared a board!
                    val nextClearedCount = current.rushLevelsCleared + 1
                    val timeBonus = 10
                    val bonusScore = ScoreCalculator.calculateRushClearBonus(nextClearedCount)
                    val nextLevelIndex = (nextClearedCount % LevelCatalog.levels.size) + 1
                    val nextLevel = LevelCatalog.getLevel(nextLevelIndex)
                    engine.loadLevel(nextLevel)

                    _gameState.update {
                        it.copy(
                            level = nextLevel,
                            board = engine.getBoardState(),
                            flyingArrowIds = emptySet(),
                            rushLevelsCleared = nextClearedCount,
                            rushTimeRemainingSeconds = (it.rushTimeRemainingSeconds + timeBonus).coerceAtMost(99),
                            rushScore = it.rushScore + bonusScore,
                            canUndo = false
                        )
                    }
                }
            } else {
                val levelFeedback = if (current.level.levelNumber == 1) {
                    when (updatedBoard.arrows.size) {
                        2 -> "Awesome! Now tap the next front arrow pointing right."
                        1 -> "Last one! Tap the final arrow to clear the level."
                        else -> null
                    }
                } else null

                _gameState.update {
                    it.copy(
                        board = updatedBoard,
                        flyingArrowIds = it.flyingArrowIds - arrow.id,
                        feedbackMessage = levelFeedback ?: it.feedbackMessage,
                        canUndo = engine.canUndo()
                    )
                }
            }
        }
    }

    fun undoLastMove() {
        val state = _gameState.value
        if (!state.canUndo || state.isCleared || state.mode == GameMode.RUSH) return

        sound.playTap()
        haptics.vibrateClick()
        hintClearJob?.cancel()
        blockerClearJob?.cancel()

        val undone = engine.undo()
        if (undone != null) {
            _gameState.update {
                it.copy(
                    board = engine.getBoardState(),
                    movesCount = (it.movesCount - 1).coerceAtLeast(0),
                    canUndo = engine.canUndo(),
                    hintArrowId = null,
                    blockedArrowId = null,
                    blockerArrowId = null,
                    feedbackMessage = null
                )
            }
        }
    }

    fun requestHint() {
        val state = _gameState.value
        if (state.isCleared || state.board.isEmpty) return

        sound.playTap()
        haptics.vibrateClick()

        val available = engine.getAvailableMoves()
        val hintArrowId = available.firstOrNull()

        if (hintArrowId != null) {
            val arrow = state.board.getArrow(hintArrowId)
            val dirLabel = arrow?.direction?.label?.uppercase() ?: "FORWARD"
            hintClearJob?.cancel()
            _gameState.update {
                it.copy(
                    hintArrowId = hintArrowId,
                    feedbackMessage = "💡 HINT: Arrow pointing $dirLabel has an open path! Tap it to escape."
                )
            }
            hintClearJob = viewModelScope.launch {
                delay(7000) // 7 seconds so player has ample time to see and understand
                _gameState.update {
                    if (it.hintArrowId == hintArrowId) {
                        it.copy(hintArrowId = null, feedbackMessage = null)
                    } else it
                }
            }
        } else {
            hintClearJob?.cancel()
            _gameState.update {
                it.copy(
                    hintArrowId = null,
                    feedbackMessage = "⚠️ No moves free right now! Try Undo or Restart."
                )
            }
            hintClearJob = viewModelScope.launch {
                delay(3000)
                _gameState.update {
                    if (it.hintArrowId == null && it.feedbackMessage?.startsWith("⚠️") == true) {
                        it.copy(feedbackMessage = null)
                    } else it
                }
            }
        }
    }

    fun restartCurrentLevel() {
        sound.playTap()
        haptics.vibrateClick()
        hintClearJob?.cancel()
        blockerClearJob?.cancel()
        moveAnimationJob?.cancel()
        if (_gameState.value.mode == GameMode.RUSH) {
            startRushMode()
        } else {
            val currentLvlNum = _gameState.value.level.levelNumber
            engine.reset()
            val resetFeedback = if (currentLvlNum == 1) {
                "💡 Welcome! Tap arrows that have a clear, open path to the board edge."
            } else null
            _gameState.update {
                it.copy(
                    board = engine.getBoardState(),
                    flyingArrowIds = emptySet(),
                    blockedArrowId = null,
                    blockerArrowId = null,
                    hintArrowId = null,
                    movesCount = 0,
                    mistakesCount = 0,
                    combo = 0,
                    isCleared = false,
                    starsEarned = 0,
                    canUndo = false,
                    feedbackMessage = resetFeedback
                )
            }
        }
    }

    fun nextLevel() {
        val nextNum = _gameState.value.level.levelNumber + 1
        if (nextNum <= 30) {
            startLevel(nextNum)
        } else {
            _currentScreen.value = Screen.LEVEL_SELECT
        }
    }
}
