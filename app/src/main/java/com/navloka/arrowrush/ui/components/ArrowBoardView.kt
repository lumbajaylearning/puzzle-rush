package com.navloka.arrowrush.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.navloka.arrowrush.core.model.Arrow
import com.navloka.arrowrush.core.model.Direction
import com.navloka.arrowrush.ui.theme.LocalGameColors
import kotlin.math.roundToInt

@Composable
fun ArrowBoardView(
    rows: Int,
    cols: Int,
    activeArrows: Map<Int, Arrow>,
    flyingArrowIds: Set<Int>,
    blockedArrowId: Int?,
    blockerArrowId: Int?,
    hintArrowId: Int?,
    onArrowClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current

    // Infinite breathing pulse for hints and board effects
    val infiniteTransition = rememberInfiniteTransition(label = "boardPulse")
    val pulseCycle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseCycle"
    )

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(cols.toFloat() / rows.toFloat())
            .clip(RoundedCornerShape(24.dp))
            .background(gameColors.boardBackground)
            .border(2.dp, gameColors.gridLine.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        val boardWidthPx = constraints.maxWidth.toFloat()
        val boardHeightPx = constraints.maxHeight.toFloat()
        val cellWidthPx = boardWidthPx / cols
        val cellHeightPx = boardHeightPx / rows

        // Grid lines, hint trajectory beam, and collision rays
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Vertical grid lines
            for (c in 1 until cols) {
                val x = c * cellWidthPx
                drawLine(
                    color = gameColors.gridLine,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1.5f
                )
            }
            // Horizontal grid lines
            for (r in 1 until rows) {
                val y = r * cellHeightPx
                drawLine(
                    color = gameColors.gridLine,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.5f
                )
            }

            // 1. Hint Escape Trajectory Beam
            if (hintArrowId != null) {
                val hintArrow = activeArrows[hintArrowId]
                if (hintArrow != null && !flyingArrowIds.contains(hintArrowId)) {
                    val startX = (hintArrow.col + 0.5f) * cellWidthPx
                    val startY = (hintArrow.row + 0.5f) * cellHeightPx
                    val endX = when (hintArrow.direction) {
                        Direction.RIGHT -> size.width + 30f
                        Direction.LEFT -> -30f
                        else -> startX
                    }
                    val endY = when (hintArrow.direction) {
                        Direction.DOWN -> size.height + 30f
                        Direction.UP -> -30f
                        else -> startY
                    }

                    // Broad glowing halo beam
                    drawLine(
                        color = Color(0xFFFBBF24).copy(alpha = 0.25f + 0.25f * pulseCycle),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 20f,
                        cap = StrokeCap.Round
                    )

                    // Core dashed laser line
                    drawLine(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFDE047),
                                Color(0xFFF59E0B)
                            ),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY)
                        ),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 6f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 10f), pulseCycle * 26f),
                        cap = StrokeCap.Round
                    )

                    // Target exit point pulse
                    val exitX = endX.coerceIn(4f, size.width - 4f)
                    val exitY = endY.coerceIn(4f, size.height - 4f)
                    drawCircle(
                        color = Color(0xFFFBBF24).copy(alpha = 0.6f + 0.4f * pulseCycle),
                        radius = 10f + 6f * pulseCycle,
                        center = Offset(exitX, exitY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 4f,
                        center = Offset(exitX, exitY)
                    )
                }
            }

            // 2. Blocked Collision Ray
            if (blockedArrowId != null && blockerArrowId != null) {
                val blocked = activeArrows[blockedArrowId]
                val blocker = activeArrows[blockerArrowId]
                if (blocked != null && blocker != null) {
                    val bStartX = (blocked.col + 0.5f) * cellWidthPx
                    val bStartY = (blocked.row + 0.5f) * cellHeightPx
                    val bEndX = (blocker.col + 0.5f) * cellWidthPx
                    val bEndY = (blocker.row + 0.5f) * cellHeightPx

                    // Red dashed collision connection
                    drawLine(
                        color = Color(0xFFEF4444).copy(alpha = 0.9f),
                        start = Offset(bStartX, bStartY),
                        end = Offset(bEndX, bEndY),
                        strokeWidth = 6f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
                        cap = StrokeCap.Round
                    )

                    // Impact flash at midpoint
                    val midX = (bStartX + bEndX) / 2f
                    val midY = (bStartY + bEndY) / 2f
                    drawCircle(
                        color = Color(0xFFEF4444).copy(alpha = 0.4f),
                        radius = 18f,
                        center = Offset(midX, midY)
                    )
                    drawCircle(
                        color = Color(0xFFF87171),
                        radius = 8f,
                        center = Offset(midX, midY)
                    )
                }
            }
        }

        // Arrow items
        for (arrow in activeArrows.values) {
            val isFlying = flyingArrowIds.contains(arrow.id)
            val isBlocked = blockedArrowId == arrow.id
            val isBlocker = blockerArrowId == arrow.id
            val isHinted = hintArrowId == arrow.id

            ArrowCellItem(
                arrow = arrow,
                cellWidthPx = cellWidthPx,
                cellHeightPx = cellHeightPx,
                boardWidthPx = boardWidthPx,
                boardHeightPx = boardHeightPx,
                isFlying = isFlying,
                isBlocked = isBlocked,
                isBlocker = isBlocker,
                isHinted = isHinted,
                pulseCycle = pulseCycle,
                onClick = { onArrowClick(arrow.id) }
            )
        }
    }
}

@Composable
private fun ArrowCellItem(
    arrow: Arrow,
    cellWidthPx: Float,
    cellHeightPx: Float,
    boardWidthPx: Float,
    boardHeightPx: Float,
    isFlying: Boolean,
    isBlocked: Boolean,
    isBlocker: Boolean,
    isHinted: Boolean,
    pulseCycle: Float,
    onClick: () -> Unit
) {
    val gameColors = LocalGameColors.current
    val baseColor = gameColors.arrowColors[arrow.colorIndex % gameColors.arrowColors.size]

    // Flying animation: flies off board in direction
    val flyOffsetAnim = remember { Animatable(0f) }
    val flyAlphaAnim = remember { Animatable(1f) }

    LaunchedEffect(isFlying) {
        if (isFlying) {
            flyOffsetAnim.animateTo(
                targetValue = maxOf(boardWidthPx, boardHeightPx) * 1.25f,
                animationSpec = tween(durationMillis = 200, easing = LinearEasing)
            )
        } else {
            flyOffsetAnim.snapTo(0f)
            flyAlphaAnim.snapTo(1f)
        }
    }

    // Shake animation on blocked
    val shakeAnim = remember { Animatable(0f) }
    LaunchedEffect(isBlocked) {
        if (isBlocked) {
            val shakeDistance = 16f
            shakeAnim.animateTo(shakeDistance, tween(40))
            shakeAnim.animateTo(-shakeDistance, tween(40))
            shakeAnim.animateTo(shakeDistance * 0.6f, tween(40))
            shakeAnim.animateTo(0f, tween(40))
        }
    }

    // Base position
    val baseX = arrow.col * cellWidthPx
    val baseY = arrow.row * cellHeightPx

    // Extra fly offset based on direction
    val flyX = when (arrow.direction) {
        Direction.RIGHT -> flyOffsetAnim.value
        Direction.LEFT -> -flyOffsetAnim.value
        else -> 0f
    }
    val flyY = when (arrow.direction) {
        Direction.DOWN -> flyOffsetAnim.value
        Direction.UP -> -flyOffsetAnim.value
        else -> 0f
    }

    // Extra shake offset
    val shakeX = when (arrow.direction) {
        Direction.LEFT, Direction.RIGHT -> shakeAnim.value * arrow.direction.dCol
        else -> shakeAnim.value
    }
    val shakeY = when (arrow.direction) {
        Direction.UP, Direction.DOWN -> shakeAnim.value * arrow.direction.dRow
        else -> 0f
    }

    val finalX = (baseX + flyX + shakeX).roundToInt()
    val finalY = (baseY + flyY + shakeY).roundToInt()

    val density = androidx.compose.ui.platform.LocalDensity.current
    val cellWidthDp = with(density) { cellWidthPx.toDp() }
    val cellHeightDp = with(density) { cellHeightPx.toDp() }
    val arrowPadding = cellWidthDp * 0.08f
    val shapeRadius = cellWidthDp * 0.26f

    val currentScale = when {
        isHinted -> 1f + 0.12f * pulseCycle
        isBlocked -> 0.96f
        else -> 1f
    }

    val borderColor = when {
        isBlocked -> Color(0xFFEF4444)
        isBlocker -> Color(0xFFF97316)
        isHinted -> Color(0xFFFBBF24)
        else -> gameColors.arrowBorder
    }

    val borderWidth = when {
        isBlocked || isBlocker -> 3.5.dp
        isHinted -> 4.dp
        else -> 1.5.dp
    }

    // Outer pulsating halo for hinted arrow
    if (isHinted && !isFlying) {
        Box(
            modifier = Modifier
                .offset { IntOffset(finalX, finalY) }
                .size(width = cellWidthDp, height = cellHeightDp)
                .scale(1f + 0.28f * pulseCycle)
                .border(
                    width = 2.5.dp,
                    color = Color(0xFFFBBF24).copy(alpha = 0.85f * (1f - pulseCycle * 0.6f)),
                    shape = RoundedCornerShape(shapeRadius + 4.dp)
                )
        )
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(finalX, finalY) }
            .size(width = cellWidthDp, height = cellHeightDp)
            .padding(arrowPadding)
            .scale(currentScale)
            .shadow(
                elevation = if (isHinted) 12.dp else 4.dp,
                shape = RoundedCornerShape(shapeRadius),
                ambientColor = if (isHinted) Color(0xFFFBBF24) else baseColor.copy(alpha = 0.5f),
                spotColor = if (isHinted) Color(0xFFF59E0B) else baseColor
            )
            .clip(RoundedCornerShape(shapeRadius))
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isHinted) {
                        listOf(
                            baseColor,
                            Color(0xFFD97706)
                        )
                    } else if (isBlocked) {
                        listOf(
                            Color(0xFFDC2626),
                            Color(0xFF991B1B)
                        )
                    } else {
                        listOf(
                            baseColor,
                            baseColor.copy(alpha = 0.85f)
                        )
                    }
                )
            )
            .border(borderWidth, borderColor, RoundedCornerShape(shapeRadius))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Color.White),
                onClick = onClick
            )
            .testTag("arrow_${arrow.id}"),
        contentAlignment = Alignment.Center
    ) {
        // Subtle internal highlight reflection
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.18f),
                        radius = size.minDimension * 0.35f,
                        center = Offset(size.width * 0.4f, size.height * 0.35f)
                    )
                }
        )

        // Arrow Icon rotated by direction angle
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Arrow pointing ${arrow.direction.label}",
            tint = Color.White,
            modifier = Modifier
                .size(cellWidthDp * 0.52f)
                .rotate(arrow.direction.angleDegrees)
        )

        // Hint Badge: Prominent "💡 TAP" tag
        if (isHinted && !isFlying) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 3.dp, y = (-3).dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFBBF24))
                    .border(1.dp, Color.White, RoundedCornerShape(6.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lightbulb,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(9.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "TAP",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }
        }

        // Blocked Indicator: ⛔ Badge
        if (isBlocked && !isFlying) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444))
                    .border(1.dp, Color.White, CircleShape)
                    .padding(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Block,
                    contentDescription = "Blocked",
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
            }
        }

        // Blocker Indicator: ⚠️ "BLOCKING" Badge
        if (isBlocker && !isFlying) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-2).dp, y = (-2).dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFF97316))
                    .border(0.75.dp, Color.White, RoundedCornerShape(4.dp))
                    .padding(horizontal = 3.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "BLOCK",
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}
