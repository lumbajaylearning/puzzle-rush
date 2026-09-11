package com.navloka.arrowrush.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.navloka.arrowrush.model.GameMode
import com.navloka.arrowrush.model.GameState
import com.navloka.arrowrush.ui.theme.LocalGameColors

@Composable
fun ControlBar(
    gameState: GameState,
    onUndoClick: () -> Unit,
    onHintClick: () -> Unit,
    onRestartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalGameColors.current
    val canUndo = gameState.canUndo && gameState.mode == GameMode.CAMPAIGN && !gameState.isCleared

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(colors.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ControlButton(
            icon = Icons.AutoMirrored.Filled.Undo,
            label = "Undo",
            enabled = canUndo,
            onClick = onUndoClick,
            testTag = "btn_undo"
        )
        ControlButton(
            icon = Icons.Filled.Lightbulb,
            label = "Hint",
            enabled = !gameState.isCleared && !gameState.isRushGameOver,
            onClick = onHintClick,
            testTag = "btn_hint"
        )
        ControlButton(
            icon = Icons.Filled.Refresh,
            label = "Restart",
            enabled = true,
            onClick = onRestartClick,
            testTag = "btn_restart"
        )
    }
}

@Composable
private fun ControlButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val colors = LocalGameColors.current
    val alpha = if (enabled) 1f else 0.28f

    Column(
        modifier = Modifier
            .size(width = 78.dp, height = 58.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = colors.accent),
                onClick = onClick
            )
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = colors.textPrimary.copy(alpha = alpha),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = colors.textSecondary.copy(alpha = alpha),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
