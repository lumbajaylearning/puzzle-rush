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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.navloka.arrowrush.model.GameState
import com.navloka.arrowrush.ui.theme.LocalGameColors

@Composable
fun RushGameOverDialog(
    gameState: GameState,
    highScore: Int,
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit
) {
    val gameColors = LocalGameColors.current
    val isNewRecord = gameState.rushScore > 0 && gameState.rushScore >= highScore

    Dialog(onDismissRequest = { /* Modal */ }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(gameColors.surface)
                .border(2.dp, gameColors.accent.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(gameColors.accent.copy(alpha = 0.15f))
                        .border(1.dp, gameColors.accent.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ElectricBolt,
                        contentDescription = null,
                        tint = gameColors.accent,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "TIME'S UP!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = gameColors.textPrimary,
                    letterSpacing = 1.sp
                )

                if (isNewRecord) {
                    Text(
                        text = "★ NEW HIGH SCORE! ★",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = gameColors.starActive,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Score card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(gameColors.surfaceVariant)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "FINAL SCORE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = gameColors.textSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${gameState.rushScore}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = gameColors.accent
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "BOARDS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = gameColors.textSecondary
                                )
                                Text(
                                    text = "${gameState.rushLevelsCleared}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = gameColors.textPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "BEST RECORD",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = gameColors.textSecondary
                                )
                                Text(
                                    text = "$highScore",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = gameColors.textPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_rush_replay"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = gameColors.accent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Play Again", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onMainMenu,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_rush_menu"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.GridView, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Main Menu", fontSize = 14.sp)
                }
            }
        }
    }
}
