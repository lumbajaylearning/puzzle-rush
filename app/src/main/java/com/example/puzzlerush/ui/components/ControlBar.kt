package com.example.puzzlerush.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.puzzlerush.model.GameMode
import com.example.puzzlerush.model.GameState
import com.example.puzzlerush.ui.theme.LocalGameColors

@Composable
fun ControlBar(
    gameState: GameState,
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    onUndoClick: () -> Unit,
    onHintClick: () -> Unit,
    onRestartClick: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleHaptics: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current
    val canUndo = gameState.undoStack.isNotEmpty() && gameState.mode == GameMode.CAMPAIGN && !gameState.isCleared

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Undo button
        ControlButton(
            icon = Icons.AutoMirrored.Filled.Undo,
            label = "Undo",
            enabled = canUndo,
            onClick = onUndoClick,
            testTag = "btn_undo"
        )

        // Hint button
        ControlButton(
            icon = Icons.Filled.Lightbulb,
            label = "Hint",
            enabled = !gameState.isCleared && !gameState.isRushGameOver,
            onClick = onHintClick,
            testTag = "btn_hint"
        )

        // Restart button
        ControlButton(
            icon = Icons.Filled.Refresh,
            label = "Reset",
            enabled = true,
            onClick = onRestartClick,
            testTag = "btn_restart"
        )

        // Sound toggle button
        ControlButton(
            icon = if (soundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
            label = if (soundEnabled) "Sound On" else "Muted",
            enabled = true,
            onClick = onToggleSound,
            testTag = "btn_toggle_sound"
        )

        // Haptics toggle button
        ControlButton(
            icon = Icons.Filled.Vibration,
            label = if (hapticsEnabled) "Vibe On" else "Vibe Off",
            enabled = true,
            onClick = onToggleHaptics,
            testTag = "btn_toggle_haptics"
        )
    }
}

@Composable
private fun ControlButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current
    val contentAlpha = if (enabled) 1f else 0.35f

    Box(
        modifier = modifier
            .size(width = 62.dp, height = 62.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(gameColors.surface.copy(alpha = if (enabled) 1f else 0.4f))
            .border(
                1.dp,
                if (enabled) gameColors.gridLine else gameColors.gridLine.copy(alpha = 0.2f),
                RoundedCornerShape(16.dp)
            )
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = gameColors.accent),
                onClick = onClick
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = gameColors.textPrimary.copy(alpha = contentAlpha),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = gameColors.textSecondary.copy(alpha = contentAlpha)
            )
        }
    }
}
