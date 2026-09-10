package com.example.puzzlerush.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.puzzlerush.data.LevelCatalog
import com.example.puzzlerush.model.PuzzleLevel
import com.example.puzzlerush.ui.theme.LocalGameColors
import com.example.puzzlerush.viewmodel.PuzzleGameViewModel
import com.example.puzzlerush.viewmodel.Screen

@Composable
fun LevelSelectScreen(
    viewModel: PuzzleGameViewModel,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current
    val highestUnlocked = viewModel.prefs.highestUnlockedLevel

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(gameColors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(Screen.MAIN_MENU) },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(gameColors.surface)
                    .border(1.dp, gameColors.gridLine, CircleShape)
                    .testTag("btn_levels_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = gameColors.textPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Select Level",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = gameColors.textPrimary
                )
                Text(
                    text = "$highestUnlocked / 30 Unlocked",
                    fontSize = 12.sp,
                    color = gameColors.textSecondary
                )
            }
        }

        // 30 Levels Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(LevelCatalog.levels) { level ->
                val isUnlocked = level.levelNumber <= highestUnlocked
                val stars = if (isUnlocked) viewModel.prefs.getLevelStars(level.levelNumber) else 0

                LevelGridCard(
                    level = level,
                    isUnlocked = isUnlocked,
                    stars = stars,
                    onClick = {
                        if (isUnlocked) {
                            viewModel.startLevel(level.levelNumber)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LevelGridCard(
    level: PuzzleLevel,
    isUnlocked: Boolean,
    stars: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isUnlocked) gameColors.surface else gameColors.surface.copy(alpha = 0.4f))
            .border(
                width = if (isUnlocked) 1.5.dp else 1.dp,
                color = if (isUnlocked) gameColors.gridLine else gameColors.gridLine.copy(alpha = 0.2f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(
                enabled = isUnlocked,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = gameColors.accent),
                onClick = onClick
            )
            .padding(vertical = 14.dp, horizontal = 8.dp)
            .testTag("level_card_${level.levelNumber}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isUnlocked) {
                Text(
                    text = "${level.levelNumber}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = gameColors.textPrimary
                )

                Text(
                    text = level.title,
                    fontSize = 10.sp,
                    color = gameColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stars row
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..3) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (i <= stars) gameColors.starActive else gameColors.starInactive,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            } else {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Locked",
                    tint = gameColors.textSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(26.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${level.levelNumber}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = gameColors.textSecondary.copy(alpha = 0.5f)
                )
            }
        }
    }
}
