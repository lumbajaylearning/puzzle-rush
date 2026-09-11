package com.navloka.arrowrush.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.navloka.arrowrush.model.GameMode
import com.navloka.arrowrush.ui.components.ArrowBoardView
import com.navloka.arrowrush.ui.components.ControlBar
import com.navloka.arrowrush.ui.components.GameHeader
import com.navloka.arrowrush.ui.components.HowToPlayDialog
import com.navloka.arrowrush.ui.components.RushGameOverDialog
import com.navloka.arrowrush.ui.components.VictoryDialog
import com.navloka.arrowrush.ui.theme.LocalGameColors
import com.navloka.arrowrush.viewmodel.PuzzleGameViewModel
import com.navloka.arrowrush.viewmodel.Screen

@Composable
fun GameScreen(
    viewModel: PuzzleGameViewModel,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current
    val gameState by viewModel.gameState.collectAsState()
    var showHowToPlay by remember { mutableStateOf(false) }

    // Auto-show tutorial for first-time players on level 1
    LaunchedEffect(Unit) {
        if (!viewModel.prefs.hasSeenTutorial && gameState.level.levelNumber == 1 && gameState.movesCount == 0) {
            showHowToPlay = true
        }
    }

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
                },
                onHelpClick = { showHowToPlay = true }
            )

            // Tutorial / Hint / Blocked Feedback Message Banner
            FeedbackBanner(
                message = gameState.feedbackMessage,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Center Board Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
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

        // How to Play Dialog
        if (showHowToPlay) {
            HowToPlayDialog(
                onDismiss = {
                    viewModel.prefs.hasSeenTutorial = true
                    showHowToPlay = false
                }
            )
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

@Composable
private fun FeedbackBanner(
    message: String?,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current

    AnimatedVisibility(
        visible = !message.isNullOrEmpty(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        if (!message.isNullOrEmpty()) {
            val isError = message.contains("BLOCKED", ignoreCase = true) || message.startsWith("🚫")
            val isHint = message.contains("HINT", ignoreCase = true) || message.startsWith("💡")

            val bannerBg = when {
                isError -> Color(0xFFEF4444).copy(alpha = 0.16f)
                isHint -> Color(0xFFFBBF24).copy(alpha = 0.18f)
                else -> gameColors.accent.copy(alpha = 0.15f)
            }
            val bannerBorder = when {
                isError -> Color(0xFFEF4444).copy(alpha = 0.7f)
                isHint -> Color(0xFFFBBF24).copy(alpha = 0.8f)
                else -> gameColors.accent.copy(alpha = 0.6f)
            }
            val bannerTextColor = when {
                isError -> Color(0xFFFCA5A5)
                isHint -> Color(0xFFFDE047)
                else -> gameColors.textPrimary
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(bannerBg)
                    .border(1.dp, bannerBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = bannerTextColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
