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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.navloka.arrowrush.core.model.Arrow
import com.navloka.arrowrush.core.model.Direction
import com.navloka.arrowrush.ui.theme.LocalGameColors
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Renders the logical board without turning every cell into a button. The board origin is the
 * top-left corner, so row/column coordinates map directly to the cells the player sees and taps.
 */
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
    val colors = LocalGameColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "boardPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "boardPulseValue"
    )

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(cols.toFloat() / rows.toFloat())
            .clip(RoundedCornerShape(32.dp))
            .background(colors.boardBackground)
            .border(1.dp, colors.gridLine.copy(alpha = 0.55f), RoundedCornerShape(32.dp))
            .padding(14.dp),
        // Center alignment shifted every logical item by one cell on a 3x3 board and put
        // Level 1's leading arrow outside the playable area.
        contentAlignment = Alignment.TopStart
    ) {
        val boardWidthPx = constraints.maxWidth.toFloat()
        val boardHeightPx = constraints.maxHeight.toFloat()
        val cellWidthPx = boardWidthPx / cols
        val cellHeightPx = boardHeightPx / rows

        Canvas(modifier = Modifier.fillMaxSize()) {
            // Quiet registration dots show spatial relationships without a heavy grid.
            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    drawCircle(
                        color = colors.gridLine.copy(alpha = 0.42f),
                        radius = (size.minDimension / maxOf(rows, cols)) * 0.025f,
                        center = Offset(
                            x = (col + 0.5f) * cellWidthPx,
                            y = (row + 0.5f) * cellHeightPx
                        )
                    )
                }
            }

            val hintArrow = hintArrowId?.let(activeArrows::get)
            if (hintArrow != null && hintArrow.id !in flyingArrowIds) {
                val start = arrowCenter(hintArrow, cellWidthPx, cellHeightPx)
                val end = boardExit(hintArrow.direction, start, size.width, size.height)
                drawLine(
                    color = colors.hintGlow.copy(alpha = 0.18f + pulse * 0.12f),
                    start = start,
                    end = end,
                    strokeWidth = 12f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = colors.hintGlow.copy(alpha = 0.72f),
                    start = start,
                    end = end,
                    strokeWidth = 3f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), pulse * 22f),
                    cap = StrokeCap.Round
                )
            }

            val blocked = blockedArrowId?.let(activeArrows::get)
            val blocker = blockerArrowId?.let(activeArrows::get)
            if (blocked != null && blocker != null) {
                drawLine(
                    color = colors.blockerHighlight.copy(alpha = 0.6f),
                    start = arrowCenter(blocked, cellWidthPx, cellHeightPx),
                    end = arrowCenter(blocker, cellWidthPx, cellHeightPx),
                    strokeWidth = 4f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)),
                    cap = StrokeCap.Round
                )
            }
        }

        activeArrows.values.sortedBy { it.id }.forEach { arrow ->
            ArrowCellItem(
                arrow = arrow,
                cellWidthPx = cellWidthPx,
                cellHeightPx = cellHeightPx,
                boardWidthPx = boardWidthPx,
                boardHeightPx = boardHeightPx,
                isFlying = arrow.id in flyingArrowIds,
                isBlocked = arrow.id == blockedArrowId,
                isBlocker = arrow.id == blockerArrowId,
                isHinted = arrow.id == hintArrowId,
                pulse = pulse,
                onClick = { onArrowClick(arrow.id) }
            )
        }
    }
}

private fun arrowCenter(arrow: Arrow, cellWidth: Float, cellHeight: Float): Offset =
    Offset((arrow.col + 0.5f) * cellWidth, (arrow.row + 0.5f) * cellHeight)

internal fun boardCellOffset(
    row: Int,
    col: Int,
    cellWidth: Float,
    cellHeight: Float
): IntOffset = IntOffset(
    x = (col * cellWidth).roundToInt(),
    y = (row * cellHeight).roundToInt()
)

private fun boardExit(
    direction: Direction,
    start: Offset,
    boardWidth: Float,
    boardHeight: Float
): Offset = when (direction) {
    Direction.RIGHT -> Offset(boardWidth, start.y)
    Direction.LEFT -> Offset(0f, start.y)
    Direction.DOWN -> Offset(start.x, boardHeight)
    Direction.UP -> Offset(start.x, 0f)
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
    pulse: Float,
    onClick: () -> Unit
) {
    val colors = LocalGameColors.current
    val density = LocalDensity.current
    val width = with(density) { cellWidthPx.toDp() }
    val height = with(density) { cellHeightPx.toDp() }
    val flyOffset = remember(arrow.id) { Animatable(0f) }
    val flyAlpha = remember(arrow.id) { Animatable(1f) }
    val shake = remember(arrow.id) { Animatable(0f) }

    LaunchedEffect(isFlying) {
        if (isFlying) {
            coroutineScope {
                launch {
                    flyOffset.animateTo(
                        targetValue = maxOf(boardWidthPx, boardHeightPx) * 1.18f,
                        animationSpec = tween(220, easing = LinearEasing)
                    )
                }
                launch {
                    flyAlpha.animateTo(0f, animationSpec = tween(220, easing = LinearEasing))
                }
            }
        } else {
            flyOffset.snapTo(0f)
            flyAlpha.snapTo(1f)
        }
    }

    LaunchedEffect(isBlocked) {
        if (isBlocked) {
            shake.animateTo(12f, tween(45))
            shake.animateTo(-10f, tween(45))
            shake.animateTo(7f, tween(45))
            shake.animateTo(0f, tween(45))
        }
    }

    val flyX = when (arrow.direction) {
        Direction.RIGHT -> flyOffset.value
        Direction.LEFT -> -flyOffset.value
        else -> 0f
    }
    val flyY = when (arrow.direction) {
        Direction.DOWN -> flyOffset.value
        Direction.UP -> -flyOffset.value
        else -> 0f
    }
    val shakeX = if (arrow.direction == Direction.LEFT || arrow.direction == Direction.RIGHT) shake.value else 0f
    val shakeY = if (arrow.direction == Direction.UP || arrow.direction == Direction.DOWN) shake.value else 0f

    val ink = when {
        isBlocked -> colors.blockerHighlight
        isBlocker -> colors.blockerHighlight.copy(alpha = 0.75f)
        isHinted -> colors.hintGlow
        else -> colors.textPrimary
    }

    Box(
        modifier = Modifier
            .offset {
                val origin = boardCellOffset(
                    row = arrow.row,
                    col = arrow.col,
                    cellWidth = cellWidthPx,
                    cellHeight = cellHeightPx
                )
                IntOffset(
                    x = origin.x + (flyX + shakeX).roundToInt(),
                    y = origin.y + (flyY + shakeY).roundToInt()
                )
            }
            .size(width = width, height = height)
            .padding(width * 0.08f)
            .alpha(flyAlpha.value)
            .scale(if (isHinted) 1f + pulse * 0.08f else 1f)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, color = colors.accent),
                onClick = onClick
            )
            .testTag("arrow_${arrow.id}"),
        contentAlignment = Alignment.Center
    ) {
        if (isHinted || isBlocked || isBlocker) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(width * 0.08f)
                    .background(
                        color = ink.copy(alpha = if (isHinted) 0.12f + pulse * 0.06f else 0.1f),
                        shape = CircleShape
                    )
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(width * 0.16f)
                .rotate(arrow.direction.angleDegrees)
        ) {
            val stroke = (size.minDimension * 0.09f).coerceAtLeast(4f)
            val tail = Offset(size.width * 0.18f, size.height * 0.5f)
            val tip = Offset(size.width * 0.82f, size.height * 0.5f)
            val headX = size.width * 0.57f
            drawLine(ink, tail, tip, strokeWidth = stroke, cap = StrokeCap.Round)
            drawLine(
                ink,
                Offset(headX, size.height * 0.27f),
                tip,
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
            drawLine(
                ink,
                Offset(headX, size.height * 0.73f),
                tip,
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        }
    }
}
