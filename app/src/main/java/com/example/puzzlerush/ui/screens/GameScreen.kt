package com.example.puzzlerush.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.puzzlerush.model.GameMode
import com.example.puzzlerush.ui.components.ArrowBoardView
import com.example.puzzlerush.ui.components.ControlBar
import com.example.puzzlerush.ui.components.GameHeader
import com.example.puzzlerush.ui.components.RushGameOverDialog
import com.example.puzzlerush.ui.components.VictoryDialog
import com.example.puzzlerush.ui.theme.LocalGameColors
import com.example.puzzlerush.viewmodel.PuzzleGameViewModel
import com.example.puzzlerush.viewmodel.Screen

@Composable
fun GameScreen(
    viewModel: PuzzleGameViewModel,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current
    val gameState by viewModel.gameState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gameColors.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 540.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            GameHeader(
                gameState = gameState,
                onBackClick = {
                    if (gameState.mode == GameMode.RUSH) {
                        viewModel.navigateTo(Screen.MAIN_MENU)
                    } else {
                        viewModel.navigateTo(Screen.LEVEL_SELECT)
                    }
                }
            )

            // Board Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                ArrowBoardView(
                    rows = gameState.level.rows,
                    cols = gameState.level.cols,
                    activeArrows = gameState.activeArrows,
                    flyingArrowIds = gameState.flyingArrowIds,
                    blockedArrowId = gameState.blockedArrowId,
                    blockerArrowId = gameState.blockerArrowId,
                    hintArrowId = gameState.hintArrowId,
                    onArrowClick = { arrowId -> viewModel.onArrowClicked(arrowId) },
                    modifier = Modifier.fillMaxWidth(0.96f)
                )
            }

            // Controls
            ControlBar(
                gameState = gameState,
                soundEnabled = viewModel.prefs.soundEnabled,
                hapticsEnabled = viewModel.prefs.hapticsEnabled,
                onUndoClick = { viewModel.undoLastMove() },
                onHintClick = { viewModel.requestHint() },
                onRestartClick = { viewModel.restartCurrentLevel() },
                onToggleSound = { viewModel.toggleSound() },
                onToggleHaptics = { viewModel.toggleHaptics() }
            )

            Spacer(modifier = Modifier.height(4.dp))
        }

        // Victory Dialog for Campaign Mode
        if (gameState.isCleared && gameState.mode == GameMode.CAMPAIGN) {
            VictoryDialog(
                gameState = gameState,
                onNextLevel = { viewModel.nextLevel() },
                onReplay = { viewModel.restartCurrentLevel() },
                onLevelSelect = { viewModel.navigateTo(Screen.LEVEL_SELECT) }
            )
        }

        // Game Over Dialog for Rush Mode
        if (gameState.isRushGameOver && gameState.mode == GameMode.RUSH) {
            RushGameOverDialog(
                gameState = gameState,
                highScore = viewModel.prefs.rushHighScore,
                onPlayAgain = { viewModel.startRushMode() },
                onMainMenu = { viewModel.navigateTo(Screen.MAIN_MENU) }
            )
        }
    }
}
