package com.example.puzzlerush.ui.components

import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.puzzlerush.model.ArrowBlock
import com.example.puzzlerush.model.Direction
import com.example.puzzlerush.ui.theme.LocalGameColors
import kotlin.math.roundToInt

@Composable
fun ArrowBoardView(
    rows: Int,
    cols: Int,
    activeArrows: Map<Int, ArrowBlock>,
    flyingArrowIds: Set<Int>,
    blockedArrowId: Int?,
    blockerArrowId: Int?,
    hintArrowId: Int?,
    onArrowClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val gameColors = LocalGameColors.current

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

        // Grid lines drawing
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Vertical lines
            for (c in 1 until cols) {
                val x = c * cellWidthPx
                drawLine(
                    color = gameColors.gridLine,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1.5f
                )
            }
            // Horizontal lines
            for (r in 1 until rows) {
                val y = r * cellHeightPx
                drawLine(
                    color = gameColors.gridLine,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.5f
                )
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
                onClick = { onArrowClick(arrow.id) }
            )
        }
    }
}

@Composable
private fun ArrowCellItem(
    arrow: ArrowBlock,
    cellWidthPx: Float,
    cellHeightPx: Float,
    boardWidthPx: Float,
    boardHeightPx: Float,
    isFlying: Boolean,
    isBlocked: Boolean,
    isBlocker: Boolean,
    isHinted: Boolean,
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
                targetValue = maxOf(boardWidthPx, boardHeightPx) * 1.2f,
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
            val shakeDistance = 14f
            shakeAnim.animateTo(shakeDistance, tween(40))
            shakeAnim.animateTo(-shakeDistance, tween(40))
            shakeAnim.animateTo(shakeDistance * 0.6f, tween(40))
            shakeAnim.animateTo(0f, tween(40))
        }
    }

    // Hint pulse
    val infiniteTransition = rememberInfiniteTransition(label = "hintPulse")
    val hintScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isHinted) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hintScale"
    )

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

    val borderColor = when {
        isBlocker -> gameColors.blockerHighlight
        isHinted -> gameColors.hintGlow
        isBlocked -> gameColors.blockerHighlight
        else -> gameColors.arrowBorder
    }

    val borderWidth = when {
        isBlocker || isBlocked -> 3.dp
        isHinted -> 3.5.dp
        else -> 1.5.dp
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(finalX, finalY) }
            .size(width = cellWidthDp, height = cellHeightDp)
            .padding(arrowPadding)
            .scale(if (isHinted) hintScale else 1f)
            .shadow(
                elevation = if (isHinted) 8.dp else 4.dp,
                shape = RoundedCornerShape(shapeRadius),
                ambientColor = baseColor.copy(alpha = 0.5f),
                spotColor = baseColor
            )
            .clip(RoundedCornerShape(shapeRadius))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        baseColor,
                        baseColor.copy(alpha = 0.85f)
                    )
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
                        color = Color.White.copy(alpha = 0.15f),
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
    }
}
