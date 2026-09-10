package com.example.puzzlerush.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.puzzlerush.data.LevelCatalog
import com.example.puzzlerush.ui.components.HowToPlayDialog
import com.example.puzzlerush.ui.theme.AppThemeMode
import com.example.puzzlerush.ui.theme.LocalGameColors
import com.example.puzzlerush.viewmodel.PuzzleGameViewModel
import com.example.puzzlerush.viewmodel.Screen

@Composable
fun MainMenuScreen(
    viewModel: PuzzleGameViewModel,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current
    var showHowToPlay by remember { mutableStateOf(false) }

    val unlockedLevel = viewModel.prefs.highestUnlockedLevel.coerceAtMost(30)
    var totalStars = 0
    for (i in 1..30) {
        totalStars += viewModel.prefs.getLevelStars(i)
    }

    // Gentle floating icon pulse
    val infiniteTransition = rememberInfiniteTransition(label = "heroPulse")
    val heroScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heroScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(gameColors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Toolbar: Sound, Haptics & Help
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Theme pill toggle
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(gameColors.surface)
                    .border(1.dp, gameColors.gridLine, RoundedCornerShape(20.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AppThemeMode.entries.forEach { mode ->
                    val isSelected = viewModel.themeMode.value == mode
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) gameColors.accent else Color.Transparent)
                            .clickable { viewModel.setTheme(mode) }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.Black else gameColors.textSecondary
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { viewModel.toggleSound() },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(gameColors.surface)
                        .border(1.dp, gameColors.gridLine, CircleShape)
                        .testTag("btn_menu_sound")
                ) {
                    Icon(
                        imageVector = if (viewModel.prefs.soundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                        contentDescription = "Toggle Sound",
                        tint = gameColors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleHaptics() },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(gameColors.surface)
                        .border(1.dp, gameColors.gridLine, CircleShape)
                        .testTag("btn_menu_haptics")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Vibration,
                        contentDescription = "Toggle Haptics",
                        tint = if (viewModel.prefs.hapticsEnabled) gameColors.accent else gameColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { showHowToPlay = true },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(gameColors.surface)
                        .border(1.dp, gameColors.gridLine, CircleShape)
                        .testTag("btn_how_to_play")
                ) {
                    Icon(
                        imageVector = Icons.Filled.HelpOutline,
                        contentDescription = "How to Play",
                        tint = gameColors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Center Hero: Arrow Rush Visual Logo
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(heroScale)
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(gameColors.surface)
                    .border(2.dp, gameColors.accent.copy(alpha = 0.6f), RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                // 4 arrows pointing in four directions
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = gameColors.arrowColors[0],
                    modifier = Modifier
                        .size(36.dp)
                        .rotate(270f) // Up
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = gameColors.arrowColors[1 % gameColors.arrowColors.size],
                    modifier = Modifier
                        .size(36.dp)
                        .rotate(0f) // Right
                        .align(Alignment.CenterEnd)
                        .padding(end = 10.dp)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = gameColors.arrowColors[2 % gameColors.arrowColors.size],
                    modifier = Modifier
                        .size(36.dp)
                        .rotate(90f) // Down
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = gameColors.arrowColors[3 % gameColors.arrowColors.size],
                    modifier = Modifier
                        .size(36.dp)
                        .rotate(180f) // Left
                        .align(Alignment.CenterStart)
                        .padding(start = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "PUZZLE RUSH",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = gameColors.textPrimary,
                letterSpacing = 2.sp
            )

            Text(
                text = "Arrow Directional Untangling • 30 Levels",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = gameColors.textSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Stars & Rush Score badges
            Row(
                modifier = Modifier.padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(gameColors.surface)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = gameColors.starActive,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$totalStars / 90",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = gameColors.textPrimary
                    )
                }

                if (viewModel.prefs.rushHighScore > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(gameColors.surface)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ElectricBolt,
                            contentDescription = null,
                            tint = gameColors.accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Best: ${viewModel.prefs.rushHighScore}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = gameColors.textPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Actions
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Play Campaign Button
            Button(
                onClick = { viewModel.startLevel(unlockedLevel) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("btn_play_campaign"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = gameColors.accent,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Play Level $unlockedLevel",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Rush Mode Button
            Button(
                onClick = { viewModel.startRushMode() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_play_rush"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = gameColors.surfaceVariant,
                    contentColor = gameColors.textPrimary
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(
                    Icons.Filled.ElectricBolt,
                    contentDescription = null,
                    tint = gameColors.accent,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Puzzle Rush (60s Blitz)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Level Select Grid Button
            OutlinedButton(
                onClick = { viewModel.navigateTo(Screen.LEVEL_SELECT) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_level_select"),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Filled.GridView, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "All 30 Levels",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Footer version info
        Text(
            text = "Puzzle Rush v1.0 • Milestone 1",
            fontSize = 11.sp,
            color = gameColors.textSecondary.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 12.dp)
        )
    }

    if (showHowToPlay) {
        HowToPlayDialog(onDismiss = { showHowToPlay = false })
    }
}
