package com.navloka.arrowrush.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.rotate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.navloka.arrowrush.ui.theme.LocalGameColors

@Composable
fun HowToPlayDialog(onDismiss: () -> Unit) {
    val gameColors = LocalGameColors.current

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(gameColors.surface)
                .border(2.dp, gameColors.gridLine, RoundedCornerShape(28.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header badge
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(gameColors.accent.copy(alpha = 0.15f))
                        .border(1.dp, gameColors.accent.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = null,
                        tint = gameColors.accent,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "HOW TO PLAY",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = gameColors.textPrimary,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Untangle the board by launching arrows off the edge!",
                    fontSize = 12.sp,
                    color = gameColors.textSecondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Rule 1: Clear Path Launches
                DiagramRuleCard(
                    title = "1. Clear Path = ESCAPE!",
                    description = "Arrows only fly in the direction they point. If there are no other arrows in front of it, tapping it launches it off the board!",
                    statusColor = Color(0xFF10B981)
                ) {
                    // Mini visual demonstration
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(gameColors.boardBackground)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        MiniArrow(direction = 0f, color = Color(0xFF38BDF8))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "➔➔➔ [EXIT]",
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Rule 2: Blocked by Arrows Ahead
                DiagramRuleCard(
                    title = "2. Blocked Path = COLLISION!",
                    description = "If another arrow sits directly in its path, the move is blocked and costs one focus heart. Three blocked taps end the run, so trace the full lane first!",
                    statusColor = Color(0xFFEF4444)
                ) {
                    // Mini visual demonstration
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(gameColors.boardBackground)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        MiniArrow(direction = 0f, color = Color(0xFF818CF8))
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Filled.Block,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        MiniArrow(direction = 0f, color = Color(0xFFFB7185))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BLOCKED!  −1 ♥",
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Rule 3: Strategy & Tools
                DiagramRuleCard(
                    title = "3. Strategy & Game Tools",
                    description = "Dense boards hide dependency chains. Find any arrow with a clear lane to an edge, then use the gap it creates to unlock the next path.",
                    statusColor = Color(0xFFFBBF24)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(gameColors.boardBackground)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ToolInfoItem(icon = Icons.Filled.Lightbulb, label = "Hint", desc = "Reveals free arrow", color = Color(0xFFFBBF24))
                        ToolInfoItem(icon = Icons.AutoMirrored.Filled.Undo, label = "Undo", desc = "Step back", color = gameColors.accent)
                        ToolInfoItem(icon = Icons.Filled.Refresh, label = "Restart", desc = "Reset board", color = Color(0xFFFB7185))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_close_help"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = gameColors.accent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Got It! Let's Play", fontWeight = FontWeight.Black, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
private fun DiagramRuleCard(
    title: String,
    description: String,
    statusColor: Color,
    content: @Composable () -> Unit
) {
    val gameColors = LocalGameColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(gameColors.surfaceVariant)
            .border(1.dp, gameColors.gridLine, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = statusColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            fontSize = 12.sp,
            color = gameColors.textSecondary,
            lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun MiniArrow(direction: Float, color: Color) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .border(1.5.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(20.dp)
                .rotate(direction)
        )
    }
}

@Composable
private fun ToolInfoItem(
    icon: ImageVector,
    label: String,
    desc: String,
    color: Color
) {
    val gameColors = LocalGameColors.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = gameColors.textPrimary)
        Text(text = desc, fontSize = 8.sp, color = gameColors.textSecondary)
    }
}
