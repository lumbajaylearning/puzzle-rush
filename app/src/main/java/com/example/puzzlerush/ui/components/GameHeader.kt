package com.example.puzzlerush.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.puzzlerush.model.GameMode
import com.example.puzzlerush.model.GameState
import com.example.puzzlerush.ui.theme.LocalGameColors

@Composable
fun GameHeader(
    gameState: GameState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(gameColors.surface)
                .border(1.dp, gameColors.gridLine, CircleShape)
                .testTag("btn_back")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = gameColors.textPrimary
            )
        }

        if (gameState.mode == GameMode.CAMPAIGN) {
            CampaignHeaderCenter(gameState = gameState)
        } else {
            RushHeaderCenter(gameState = gameState)
        }

        // Remaining arrows counter pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(gameColors.surface)
                .border(1.dp, gameColors.gridLine, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${gameState.activeArrows.size}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = gameColors.accent
                )
                Text(
                    text = "LEFT",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = gameColors.textSecondary,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun CampaignHeaderCenter(
    gameState: GameState,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Level ${gameState.level.levelNumber} • ${gameState.level.title}",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = gameColors.textPrimary
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            // Stars estimate
            val starsEarned = when {
                gameState.mistakesCount == 0 && gameState.movesCount <= gameState.level.targetMoves -> 3
                gameState.mistakesCount <= 2 -> 2
                else -> 1
            }

            for (i in 1..3) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (i <= starsEarned) gameColors.starActive else gameColors.starInactive,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Moves: ${gameState.movesCount}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = gameColors.textSecondary
            )
        }
    }
}

@Composable
private fun RushHeaderCenter(
    gameState: GameState,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current
    val isUrgent = gameState.rushTimeRemainingSeconds <= 10

    val timerBgColor by animateColorAsState(
        targetValue = if (isUrgent) Color(0xFFEF4444) else gameColors.surface,
        animationSpec = tween(300),
        label = "timerBgColor"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Timer pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(timerBgColor)
                    .border(
                        1.5.dp,
                        if (isUrgent) Color.White else gameColors.gridLine,
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Timer,
                    contentDescription = "Timer",
                    tint = if (isUrgent) Color.White else gameColors.accent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${gameState.rushTimeRemainingSeconds}s",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = if (isUrgent) Color.White else gameColors.textPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Score
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "${gameState.rushScore} pts",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = gameColors.textPrimary
                )
                Text(
                    text = "Cleared: ${gameState.rushLevelsCleared}",
                    fontSize = 11.sp,
                    color = gameColors.textSecondary
                )
            }
        }

        // Combo Tag
        AnimatedVisibility(visible = gameState.rushCombo >= 2) {
            Text(
                text = "${gameState.rushCombo}x COMBO!",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = gameColors.accent,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
