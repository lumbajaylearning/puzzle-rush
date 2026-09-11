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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.navloka.arrowrush.core.model.Arrow
import com.navloka.arrowrush.core.model.Direction
import com.navloka.arrowrush.core.model.Position
import com.navloka.arrowrush.ui.theme.LocalGameColors
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Renders each logical arrow as one tappable straight or bent path. */
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
    val currentOnArrowClick by rememberUpdatedState(onArrowClick)
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
            .clip(RoundedCornerShape(28.dp))
            .background(colors.boardBackground)
            .border(1.dp, colors.gridLine.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
            .padding(12.dp),
        contentAlignment = Alignment.TopStart
    ) {
        val boardWidthPx = constraints.maxWidth.toFloat()
        val boardHeightPx = constraints.maxHeight.toFloat()
        val cellWidthPx = boardWidthPx / cols
        val cellHeightPx = boardHeightPx / rows

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(activeArrows, flyingArrowIds, rows, cols) {
                    if (flyingArrowIds.isEmpty()) {
                        detectTapGestures { tapPosition ->
                            findArrowAtBoardPosition(
                                position = tapPosition,
                                activeArrows = activeArrows,
                                rows = rows,
                                cols = cols,
                                boardWidth = boardWidthPx,
                                boardHeight = boardHeightPx
                            )?.let(currentOnArrowClick)
                        }
                    }
                }
        ) {
            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    drawCircle(
                        color = colors.gridLine.copy(alpha = 0.28f),
                        radius = (size.minDimension / maxOf(rows, cols)) * 0.018f,
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
                    color = colors.hintGlow.copy(alpha = 0.58f),
                    start = start,
                    end = end,
                    strokeWidth = 3f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), pulse * 22f),
                    cap = StrokeCap.Round
                )
            }
        }

        activeArrows.values.sortedBy { it.id }.forEach { arrow ->
            ArrowPathItem(
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
                onActivate = { onArrowClick(arrow.id) }
            )
        }
    }
}

private fun arrowCenter(arrow: Arrow, cellWidth: Float, cellHeight: Float): Offset =
    Offset((arrow.col + 0.5f) * cellWidth, (arrow.row + 0.5f) * cellHeight)

internal fun findArrowAtBoardPosition(
    position: Offset,
    activeArrows: Map<Int, Arrow>,
    rows: Int,
    cols: Int,
    boardWidth: Float,
    boardHeight: Float
): Int? {
    if (rows <= 0 || cols <= 0 || boardWidth <= 0f || boardHeight <= 0f) return null
    if (position.x < 0f || position.x >= boardWidth || position.y < 0f || position.y >= boardHeight) {
        return null
    }

    val cell = Position(
        row = (position.y / (boardHeight / rows)).toInt(),
        col = (position.x / (boardWidth / cols)).toInt()
    )
    return activeArrows.values.firstOrNull { cell in it.occupiedPositions }?.id
}

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
private fun ArrowPathItem(
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
    onActivate: () -> Unit
) {
    val colors = LocalGameColors.current
    val density = LocalDensity.current
    val minRow = arrow.path.minOf { it.row }
    val maxRow = arrow.path.maxOf { it.row }
    val minCol = arrow.path.minOf { it.col }
    val maxCol = arrow.path.maxOf { it.col }
    val widthPx = (maxCol - minCol + 1) * cellWidthPx
    val heightPx = (maxRow - minRow + 1) * cellHeightPx
    val width = with(density) { widthPx.toDp() }
    val height = with(density) { heightPx.toDp() }
    val flyOffset = remember(arrow.id) { Animatable(0f) }
    val flyAlpha = remember(arrow.id) { Animatable(1f) }
    val shake = remember(arrow.id) { Animatable(0f) }

    LaunchedEffect(isFlying) {
        if (isFlying) {
            coroutineScope {
                launch {
                    flyOffset.animateTo(
                        targetValue = maxOf(boardWidthPx, boardHeightPx) * 1.2f,
                        animationSpec = tween(260, easing = LinearEasing)
                    )
                }
                launch { flyAlpha.animateTo(0f, tween(260, easing = LinearEasing)) }
            }
        } else {
            flyOffset.snapTo(0f)
            flyAlpha.snapTo(1f)
        }
    }

    LaunchedEffect(isBlocked) {
        if (isBlocked) {
            shake.animateTo(10f, tween(45))
            shake.animateTo(-8f, tween(45))
            shake.animateTo(5f, tween(45))
            shake.animateTo(0f, tween(45))
        }
    }

    val directionX = arrow.direction.dCol.toFloat()
    val directionY = arrow.direction.dRow.toFloat()
    val origin = boardCellOffset(minRow, minCol, cellWidthPx, cellHeightPx)
    val ink = when {
        isBlocked -> colors.blockerHighlight
        isBlocker -> colors.blockerHighlight.copy(alpha = 0.78f)
        isHinted -> colors.hintGlow
        else -> colors.textPrimary
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = origin.x + (directionX * (flyOffset.value + shake.value)).roundToInt(),
                    y = origin.y + (directionY * (flyOffset.value + shake.value)).roundToInt()
                )
            }
            .size(width, height)
            .alpha(flyAlpha.value)
            .scale(if (isHinted) 1f + pulse * 0.035f else 1f)
            .semantics {
                contentDescription = "Arrow path pointing ${arrow.direction.label}"
                onClick {
                    if (!isFlying) onActivate()
                    !isFlying
                }
            }
            .testTag("arrow_${arrow.id}")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellSize = minOf(cellWidthPx, cellHeightPx)
            val stroke = (cellSize * 0.16f).coerceAtLeast(4f)
            val points = arrow.path.map { cell ->
                Offset(
                    x = (cell.col - minCol + 0.5f) * cellWidthPx,
                    y = (cell.row - minRow + 0.5f) * cellHeightPx
                )
            }

            if (isHinted || isBlocked || isBlocker) {
                points.zipWithNext().forEach { (start, end) ->
                    drawLine(
                        color = ink.copy(alpha = if (isHinted) 0.18f + pulse * 0.08f else 0.16f),
                        start = start,
                        end = end,
                        strokeWidth = stroke * 2.1f,
                        cap = StrokeCap.Round
                    )
                }
            }

            points.zipWithNext().forEach { (start, end) ->
                drawLine(ink, start, end, strokeWidth = stroke, cap = StrokeCap.Round)
            }

            val head = points.last()
            val shaftStart = if (points.size == 1) {
                Offset(
                    head.x - directionX * cellSize * 0.38f,
                    head.y - directionY * cellSize * 0.38f
                )
            } else {
                points[points.lastIndex - 1]
            }
            val tip = Offset(
                head.x + directionX * cellSize * 0.32f,
                head.y + directionY * cellSize * 0.32f
            )
            drawLine(ink, shaftStart, tip, strokeWidth = stroke, cap = StrokeCap.Round)

            val base = Offset(
                head.x - directionX * cellSize * 0.02f,
                head.y - directionY * cellSize * 0.02f
            )
            val perpendicularX = -directionY
            val perpendicularY = directionX
            val wing = cellSize * 0.23f
            val arrowHead = Path().apply {
                moveTo(tip.x, tip.y)
                lineTo(base.x + perpendicularX * wing, base.y + perpendicularY * wing)
                lineTo(base.x - perpendicularX * wing, base.y - perpendicularY * wing)
                close()
            }
            drawPath(arrowHead, ink)
        }
    }
}
