package com.example.routemaster.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.routemaster.data.BoardThemeSetting
import com.example.routemaster.model.Direction
import com.example.routemaster.model.RouteTile
import com.example.routemaster.ui.theme.BoardBgMidnight
import com.example.routemaster.ui.theme.CellBlockedHatchClassic
import com.example.routemaster.ui.theme.CellBlockedHatchMidnight
import com.example.routemaster.ui.theme.Graphite950
import com.example.routemaster.ui.theme.TileBodyDark
import com.example.routemaster.ui.theme.TileBodyDarkRaised
import com.example.routemaster.ui.theme.TileBorderActive
import com.example.routemaster.ui.theme.TileBorderDark
import com.example.routemaster.ui.theme.TrackBronze
import com.example.routemaster.ui.theme.TrackHighlightBorder
import com.example.routemaster.ui.theme.TrackHighlightGlow
import com.example.routemaster.ui.theme.TrackHighlightTeal
import com.example.routemaster.ui.theme.TrackIvory

/**
 * Draws an individual Route Tile with architectural tracks, bevels, and openings.
 */
@Composable
fun RouteTileCanvas(
    tile: RouteTile,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false,
    isSelected: Boolean = false,
    boardTheme: BoardThemeSetting = BoardThemeSetting.MIDNIGHT
) {
    val connections = tile.getConnections()
    val desc = "${tile.kind.displayName} route tile, rotated ${tile.rotation} degrees" +
            (if (isHighlighted) ", in longest route" else "") +
            (if (isSelected) ", selected" else "")

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .semantics { contentDescription = desc }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f
            val cornerRad = w * 0.14f

            // 1. Tile Base Surface
            val surfaceColor = if (isSelected) {
                TileBodyDarkRaised
            } else if (isHighlighted) {
                Color(0xFF1B2A36)
            } else {
                TileBodyDark
            }

            // Tile background with rounded corners
            drawRoundRect(
                color = surfaceColor,
                topLeft = Offset(w * 0.04f, h * 0.04f),
                size = Size(w * 0.92f, h * 0.92f),
                cornerRadius = CornerRadius(cornerRad, cornerRad)
            )

            // Outer Border
            val borderColor = when {
                isSelected -> TileBorderActive
                isHighlighted -> TrackHighlightBorder
                else -> TileBorderDark
            }
            val borderWidth = if (isSelected || isHighlighted) w * 0.045f else w * 0.025f

            drawRoundRect(
                color = borderColor,
                topLeft = Offset(w * 0.04f, h * 0.04f),
                size = Size(w * 0.92f, h * 0.92f),
                cornerRadius = CornerRadius(cornerRad, cornerRad),
                style = Stroke(width = borderWidth)
            )

            // If highlighted, subtle inner glow border
            if (isHighlighted) {
                drawRoundRect(
                    color = TrackHighlightGlow,
                    topLeft = Offset(w * 0.07f, h * 0.07f),
                    size = Size(w * 0.86f, h * 0.86f),
                    cornerRadius = CornerRadius(cornerRad * 0.8f, cornerRad * 0.8f),
                    style = Stroke(width = w * 0.04f)
                )
            }

            // 2. Track Grooves
            val trackOuterWidth = w * 0.28f
            val trackInnerWidth = w * 0.16f

            // Recessed groove channel (Dark depth)
            val grooveColor = Graphite950
            drawTileTrackNetwork(
                connections = connections,
                cx = cx,
                cy = cy,
                w = w,
                h = h,
                trackWidth = trackOuterWidth,
                color = grooveColor
            )

            // Track Foreground Core
            val trackCoreColor = when {
                isHighlighted -> TrackHighlightTeal
                boardTheme == BoardThemeSetting.CLASSIC -> TrackBronze
                else -> TrackIvory
            }

            drawTileTrackNetwork(
                connections = connections,
                cx = cx,
                cy = cy,
                w = w,
                h = h,
                trackWidth = trackInnerWidth,
                color = trackCoreColor
            )

            // 3. Center Junction Pin / Architectural Rivet
            drawCircle(
                color = grooveColor,
                radius = trackInnerWidth * 0.45f,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = if (isHighlighted) TrackHighlightTeal else surfaceColor,
                radius = trackInnerWidth * 0.22f,
                center = Offset(cx, cy)
            )

            // 4. Connection indicators at tile edges
            for (dir in connections) {
                drawConnectionOpeningNotch(dir, cx, cy, w, h, trackInnerWidth, trackCoreColor)
            }
        }
    }
}

private fun DrawScope.drawTileTrackNetwork(
    connections: Set<Direction>,
    cx: Float,
    cy: Float,
    w: Float,
    h: Float,
    trackWidth: Float,
    color: Color
) {
    val centerRadius = trackWidth / 2f

    // Draw central hub
    drawCircle(
        color = color,
        radius = centerRadius,
        center = Offset(cx, cy)
    )

    // Draw branches to connected edges
    if (Direction.UP in connections) {
        drawLine(
            color = color,
            start = Offset(cx, cy),
            end = Offset(cx, 0f),
            strokeWidth = trackWidth,
            cap = StrokeCap.Square
        )
    }
    if (Direction.RIGHT in connections) {
        drawLine(
            color = color,
            start = Offset(cx, cy),
            end = Offset(w, cy),
            strokeWidth = trackWidth,
            cap = StrokeCap.Square
        )
    }
    if (Direction.DOWN in connections) {
        drawLine(
            color = color,
            start = Offset(cx, cy),
            end = Offset(cx, h),
            strokeWidth = trackWidth,
            cap = StrokeCap.Square
        )
    }
    if (Direction.LEFT in connections) {
        drawLine(
            color = color,
            start = Offset(cx, cy),
            end = Offset(0f, cy),
            strokeWidth = trackWidth,
            cap = StrokeCap.Square
        )
    }
}

private fun DrawScope.drawConnectionOpeningNotch(
    direction: Direction,
    cx: Float,
    cy: Float,
    w: Float,
    h: Float,
    trackWidth: Float,
    color: Color
) {
    val notchLen = w * 0.06f
    when (direction) {
        Direction.UP -> {
            drawLine(
                color = color,
                start = Offset(cx - trackWidth / 2f, 0f),
                end = Offset(cx + trackWidth / 2f, 0f),
                strokeWidth = notchLen,
                cap = StrokeCap.Round
            )
        }
        Direction.RIGHT -> {
            drawLine(
                color = color,
                start = Offset(w, cy - trackWidth / 2f),
                end = Offset(w, cy + trackWidth / 2f),
                strokeWidth = notchLen,
                cap = StrokeCap.Round
            )
        }
        Direction.DOWN -> {
            drawLine(
                color = color,
                start = Offset(cx - trackWidth / 2f, h),
                end = Offset(cx + trackWidth / 2f, h),
                strokeWidth = notchLen,
                cap = StrokeCap.Round
            )
        }
        Direction.LEFT -> {
            drawLine(
                color = color,
                start = Offset(0f, cy - trackWidth / 2f),
                end = Offset(0f, cy + trackWidth / 2f),
                strokeWidth = notchLen,
                cap = StrokeCap.Round
            )
        }
    }
}
