package com.example.puzzlerush.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.puzzlerush.audio.HapticFeedbackManager
import com.example.puzzlerush.audio.SoundSynthesizer
import com.example.puzzlerush.data.GamePreferences
import com.example.puzzlerush.data.LevelCatalog
import com.example.puzzlerush.model.ArrowBlock
import com.example.puzzlerush.model.GameMode
import com.example.puzzlerush.model.GameState
import com.example.puzzlerush.model.PuzzleLevel
import com.example.puzzlerush.ui.theme.AppThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Screen {
    MAIN_MENU,
    LEVEL_SELECT,
    GAMEPLAY
}

class PuzzleGameViewModel(application: Application) : AndroidViewModel(application) {

    val prefs = GamePreferences(application)
    val sound = SoundSynthesizer().apply { isEnabled = prefs.soundEnabled }
    val haptics = HapticFeedbackManager(application).apply { isEnabled = prefs.hapticsEnabled }

    private val _currentScreen = MutableStateFlow(Screen.MAIN_MENU)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _themeMode = MutableStateFlow(
        try {
            AppThemeMode.valueOf(prefs.currentTheme)
        } catch (_: Exception) {
            AppThemeMode.CYBER
        }
    )
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val initialLevel = LevelCatalog.getLevel(prefs.highestUnlockedLevel.coerceAtMost(30))
    private val _gameState = MutableStateFlow(
        GameState(
            level = initialLevel,
            activeArrows = initialLevel.arrows.associateBy { it.id }
        )
    )
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var rushTimerJob: Job? = null
    private var blockerClearJob: Job? = null
    private var hintClearJob: Job? = null

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
        val lvl = LevelCatalog.getLevel(levelNumber)
        _gameState.value = GameState(
            level = lvl,
            mode = GameMode.CAMPAIGN,
            activeArrows = lvl.arrows.associateBy { it.id },
            flyingArrowIds = emptySet(),
            blockedArrowId = null,
            blockerArrowId = null,
            hintArrowId = null,
            movesCount = 0,
            mistakesCount = 0,
            isCleared = false,
            starsEarned = 0,
            undoStack = emptyList()
        )
        _currentScreen.value = Screen.GAMEPLAY
    }

    fun startRushMode() {
        rushTimerJob?.cancel()
        val firstRushLvl = LevelCatalog.getLevel(1)
        _gameState.value = GameState(
            level = firstRushLvl,
            mode = GameMode.RUSH,
            activeArrows = firstRushLvl.arrows.associateBy { it.id },
            flyingArrowIds = emptySet(),
            blockedArrowId = null,
            blockerArrowId = null,
            hintArrowId = null,
            movesCount = 0,
            mistakesCount = 0,
            isCleared = false,
            starsEarned = 0,
            undoStack = emptyList(),
            rushTimeRemainingSeconds = 60,
            rushScore = 0,
            rushCombo = 0,
            rushLevelsCleared = 0,
            isRushGameOver = false
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
        val newMaxCombo = maxOf(finalState.rushCombo, prefs.rushMaxCombo)
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

        val arrow = state.activeArrows[arrowId] ?: return
        if (state.flyingArrowIds.contains(arrowId)) return

        val activePositions = state.activeArrows.values.map { Pair(it.row, it.col) }.toSet()
        val isUnblocked = LevelCatalog.isArrowUnblocked(
            arrow = arrow,
            activePositions = activePositions,
            rows = state.level.rows,
            cols = state.level.cols
        )

        if (isUnblocked) {
            val nextCombo = if (state.mode == GameMode.RUSH) state.rushCombo + 1 else 0
            val addedScore = if (state.mode == GameMode.RUSH) 100 * nextCombo else 0

            if (state.mode == GameMode.RUSH) {
                sound.playCombo(nextCombo)
            } else {
                sound.playWhoosh()
            }
            haptics.vibrateClick()

            // Trigger flying animation
            _gameState.update {
                it.copy(
                    flyingArrowIds = it.flyingArrowIds + arrowId,
                    blockedArrowId = null,
                    blockerArrowId = null,
                    hintArrowId = if (it.hintArrowId == arrowId) null else it.hintArrowId,
                    movesCount = it.movesCount + 1,
                    rushScore = it.rushScore + addedScore,
                    rushCombo = nextCombo
                )
            }

            viewModelScope.launch {
                delay(220) // Duration of arrow fly-off
                val current = _gameState.value
                val remainingArrows = current.activeArrows - arrowId
                val newUndoStack = current.undoStack + arrow

                if (remainingArrows.isEmpty()) {
                    // Level cleared!
                    sound.playWin()
                    haptics.vibrateSuccess()

                    if (current.mode == GameMode.CAMPAIGN) {
                        val stars = when {
                            current.mistakesCount == 0 && current.movesCount + 1 <= current.level.targetMoves -> 3
                            current.mistakesCount <= 2 -> 2
                            else -> 1
                        }
                        prefs.setLevelStars(current.level.levelNumber, stars)
                        prefs.setBestMoves(current.level.levelNumber, current.movesCount + 1)
                        if (current.level.levelNumber >= prefs.highestUnlockedLevel && current.level.levelNumber < 30) {
                            prefs.highestUnlockedLevel = current.level.levelNumber + 1
                        }

                        _gameState.update {
                            it.copy(
                                activeArrows = remainingArrows,
                                flyingArrowIds = it.flyingArrowIds - arrowId,
                                isCleared = true,
                                starsEarned = stars,
                                undoStack = newUndoStack
                            )
                        }
                    } else {
                        // Rush Mode: cleared a board!
                        val nextClearedCount = current.rushLevelsCleared + 1
                        val timeBonus = 10
                        val bonusScore = 1000 + (nextClearedCount * 100)
                        val nextLevelIndex = (nextClearedCount % LevelCatalog.levels.size) + 1
                        val nextLevel = LevelCatalog.getLevel(nextLevelIndex)

                        _gameState.update {
                            it.copy(
                                level = nextLevel,
                                activeArrows = nextLevel.arrows.associateBy { a -> a.id },
                                flyingArrowIds = emptySet(),
                                rushLevelsCleared = nextClearedCount,
                                rushTimeRemainingSeconds = (it.rushTimeRemainingSeconds + timeBonus).coerceAtMost(99),
                                rushScore = it.rushScore + bonusScore,
                                undoStack = emptyList()
                            )
                        }
                    }
                } else {
                    _gameState.update {
                        it.copy(
                            activeArrows = remainingArrows,
                            flyingArrowIds = it.flyingArrowIds - arrowId,
                            undoStack = newUndoStack
                        )
                    }
                }
            }
        } else {
            // Blocked!
            sound.playBump()
            haptics.vibrateBump()

            val blocker = LevelCatalog.findFirstBlocker(
                arrow = arrow,
                activeArrows = state.activeArrows,
                rows = state.level.rows,
                cols = state.level.cols
            )

            blockerClearJob?.cancel()
            _gameState.update {
                it.copy(
                    blockedArrowId = arrowId,
                    blockerArrowId = blocker?.id,
                    mistakesCount = it.mistakesCount + 1,
                    rushCombo = 0
                )
            }

            blockerClearJob = viewModelScope.launch {
                delay(450)
                _gameState.update {
                    if (it.blockedArrowId == arrowId) {
                        it.copy(blockedArrowId = null, blockerArrowId = null)
                    } else it
                }
            }
        }
    }

    fun undoLastMove() {
        val state = _gameState.value
        if (state.undoStack.isEmpty() || state.isCleared || state.mode == GameMode.RUSH) return

        sound.playTap()
        haptics.vibrateClick()

        val lastArrow = state.undoStack.last()
        val newUndoStack = state.undoStack.dropLast(1)
        val newActive = state.activeArrows + (lastArrow.id to lastArrow)

        _gameState.update {
            it.copy(
                activeArrows = newActive,
                undoStack = newUndoStack,
                movesCount = (it.movesCount - 1).coerceAtLeast(0),
                hintArrowId = null,
                blockedArrowId = null,
                blockerArrowId = null
            )
        }
    }

    fun requestHint() {
        val state = _gameState.value
        if (state.isCleared || state.activeArrows.isEmpty()) return

        sound.playTap()
        haptics.vibrateClick()

        val activePositions = state.activeArrows.values.map { Pair(it.row, it.col) }.toSet()
        val unblockedArrow = state.activeArrows.values.firstOrNull { arrow ->
            LevelCatalog.isArrowUnblocked(arrow, activePositions, state.level.rows, state.level.cols)
        }

        if (unblockedArrow != null) {
            hintClearJob?.cancel()
            _gameState.update { it.copy(hintArrowId = unblockedArrow.id) }
            hintClearJob = viewModelScope.launch {
                delay(2500)
                _gameState.update {
                    if (it.hintArrowId == unblockedArrow.id) it.copy(hintArrowId = null) else it
                }
            }
        }
    }

    fun restartCurrentLevel() {
        sound.playTap()
        haptics.vibrateClick()
        if (_gameState.value.mode == GameMode.RUSH) {
            startRushMode()
        } else {
            startLevel(_gameState.value.level.levelNumber)
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
