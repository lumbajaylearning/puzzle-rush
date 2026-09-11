package com.navloka.arrowrush.ui.screens

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
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
import com.navloka.arrowrush.ui.components.HowToPlayDialog
import com.navloka.arrowrush.ui.theme.AppThemeMode
import com.navloka.arrowrush.ui.theme.LocalGameColors
import com.navloka.arrowrush.viewmodel.PuzzleGameViewModel
import com.navloka.arrowrush.viewmodel.Screen

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
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top action bar: Theme picker + Sound/Haptics + Help
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Theme selector pills
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ThemePill(
                    name = "Cyber",
                    selected = viewModel.themeMode.value == AppThemeMode.CYBER,
                    onClick = { viewModel.setTheme(AppThemeMode.CYBER) }
                )
                ThemePill(
                    name = "Zen",
                    selected = viewModel.themeMode.value == AppThemeMode.ZEN,
                    onClick = { viewModel.setTheme(AppThemeMode.ZEN) }
                )
                ThemePill(
                    name = "Sunset",
                    selected = viewModel.themeMode.value == AppThemeMode.SUNSET,
                    onClick = { viewModel.setTheme(AppThemeMode.SUNSET) }
                )
            }

            // Audio & Help icons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.toggleSound() },
                    modifier = Modifier.testTag("btn_menu_sound")
                ) {
                    Icon(
                        imageVector = if (viewModel.prefs.soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                        contentDescription = "Sound toggle",
                        tint = gameColors.textSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleHaptics() },
                    modifier = Modifier.testTag("btn_menu_haptics")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Vibration,
                        contentDescription = "Haptics toggle",
                        tint = if (viewModel.prefs.hapticsEnabled) gameColors.accent else gameColors.textSecondary.copy(alpha = 0.4f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = { showHowToPlay = true },
                    modifier = Modifier.testTag("btn_menu_help")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = "How to play",
                        tint = gameColors.textSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Hero Logo Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 24.dp)
        ) {
            // Stylized 4-directional arrow badge
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(heroScale)
                    .clip(RoundedCornerShape(32.dp))
                    .background(gameColors.surface)
                    .border(2.dp, gameColors.gridLine, RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(gameColors.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    // 4 rotating arrows forming a dynamic emblem
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = gameColors.arrowColors[0],
                        modifier = Modifier
                            .size(34.dp)
                            .rotate(0f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = gameColors.arrowColors[1 % gameColors.arrowColors.size],
                        modifier = Modifier
                            .size(34.dp)
                            .rotate(90f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = gameColors.arrowColors[2 % gameColors.arrowColors.size],
                        modifier = Modifier
                            .size(34.dp)
                            .rotate(180f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = gameColors.arrowColors[3 % gameColors.arrowColors.size],
                        modifier = Modifier
                            .size(34.dp)
                            .rotate(270f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "ARROW RUSH",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = gameColors.textPrimary,
                letterSpacing = 3.sp
            )

            Text(
                text = "DIRECTIONAL LOGIC PUZZLE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = gameColors.accent,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Stats Overview Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(gameColors.surface)
                .border(1.dp, gameColors.gridLine, RoundedCornerShape(18.dp))
                .padding(vertical = 14.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "CAMPAIGN",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = gameColors.textSecondary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "$unlockedLevel / 30",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = gameColors.textPrimary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "STARS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = gameColors.textSecondary,
                    letterSpacing = 1.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = gameColors.starActive,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "$totalStars / 90",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = gameColors.textPrimary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "RUSH RECORD",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = gameColors.textSecondary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${viewModel.prefs.rushHighScore} pts",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = gameColors.accent
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main Navigation Actions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Play / Continue Button
            Button(
                onClick = {
                    val targetLevel = viewModel.prefs.highestUnlockedLevel.coerceAtMost(30)
                    viewModel.startLevel(targetLevel)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("btn_play"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = gameColors.accent,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (unlockedLevel == 1) "PLAY" else "CONTINUE (LEVEL $unlockedLevel)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            // Levels Grid Button
            OutlinedButton(
                onClick = { viewModel.navigateTo(Screen.LEVEL_SELECT) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_levels"),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = gameColors.textPrimary
                ),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, gameColors.gridLine)
            ) {
                Icon(
                    imageVector = Icons.Filled.GridView,
                    contentDescription = null,
                    tint = gameColors.accent,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LEVELS (1 - 30)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // Rush Mode Button
            Button(
                onClick = { viewModel.startRushMode() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_rush_mode"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = gameColors.surfaceVariant,
                    contentColor = gameColors.textPrimary
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ElectricBolt,
                    contentDescription = null,
                    tint = gameColors.starActive,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RUSH MODE (60s BLITZ)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // How to Play Guide Button
            androidx.compose.material3.TextButton(
                onClick = { showHowToPlay = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_menu_how_to_play")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = null,
                    tint = gameColors.accent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "HOW TO PLAY & RULES",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = gameColors.accent,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showHowToPlay) {
        HowToPlayDialog(onDismiss = { showHowToPlay = false })
    }
}

@Composable
private fun ThemePill(
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val gameColors = LocalGameColors.current

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) gameColors.accent else gameColors.surface)
            .border(
                1.dp,
                if (selected) gameColors.accent else gameColors.gridLine,
                RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("theme_pill_$name")
    ) {
        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.Black else gameColors.textSecondary
        )
    }
}
