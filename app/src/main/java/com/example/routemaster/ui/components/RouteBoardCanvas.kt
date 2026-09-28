package com.example.routemaster.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.routemaster.data.BoardThemeSetting
import com.example.routemaster.model.BoardConstants
import com.example.routemaster.model.GridPosition
import com.example.routemaster.model.RouteCell
import com.example.routemaster.ui.theme.BoardBgClassic
import com.example.routemaster.ui.theme.BoardBgMidnight
import com.example.routemaster.ui.theme.BoardBorderClassic
import com.example.routemaster.ui.theme.BoardBorderMidnight
import com.example.routemaster.ui.theme.CellBlockedClassic
import com.example.routemaster.ui.theme.CellBlockedHatchClassic
import com.example.routemaster.ui.theme.CellBlockedHatchMidnight
import com.example.routemaster.ui.theme.CellBlockedMidnight
import com.example.routemaster.ui.theme.CellEmptyBorderClassic
import com.example.routemaster.ui.theme.CellEmptyBorderMidnight
import com.example.routemaster.ui.theme.CellEmptyClassic
import com.example.routemaster.ui.theme.CellEmptyMidnight

@Composable
fun RouteBoard(
    board: List<List<RouteCell>>,
    longestRouteCells: Set<GridPosition>,
    highlightEnabled: Boolean,
    boardTheme: BoardThemeSetting,
    lastPlacedPosition: GridPosition?,
    onCellClick: (GridPosition) -> Unit,
    modifier: Modifier = Modifier
) {
    val boardBg = if (boardTheme == BoardThemeSetting.CLASSIC) BoardBgClassic else BoardBgMidnight
    val boardBorder = if (boardTheme == BoardThemeSetting.CLASSIC) BoardBorderClassic else BoardBorderMidnight

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(boardBg)
            .border(2.dp, boardBorder, RoundedCornerShape(14.dp))
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (r in 0 until BoardConstants.GRID_SIZE) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (c in 0 until BoardConstants.GRID_SIZE) {
                        val pos = GridPosition(r, c)
                        val cell = board[r][c]
                        val isHighlighted = highlightEnabled && pos in longestRouteCells
                        val isNewlyPlaced = pos == lastPlacedPosition

                        BoardCellItem(
                            cell = cell,
                            position = pos,
                            isHighlighted = isHighlighted,
                            isNewlyPlaced = isNewlyPlaced,
                            boardTheme = boardTheme,
                            onCellClick = { onCellClick(pos) },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardCellItem(
    cell: RouteCell,
    position: GridPosition,
    isHighlighted: Boolean,
    isNewlyPlaced: Boolean,
    boardTheme: BoardThemeSetting,
    onCellClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleAnim = remember { Animatable(if (isNewlyPlaced) 0.82f else 1f) }

    LaunchedEffect(cell) {
        if (isNewlyPlaced && cell is RouteCell.Tile) {
            scaleAnim.snapTo(0.82f)
            scaleAnim.animateTo(1f, animationSpec = tween(durationMillis = 160))
        }
    }

    Box(
        modifier = modifier
            .scale(scaleAnim.value)
            .clip(RoundedCornerShape(6.dp))
            .clickable(
                enabled = cell is RouteCell.Empty,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onCellClick
            )
    ) {
        when (cell) {
            is RouteCell.Empty -> {
                EmptyCellCanvas(
                    position = position,
                    boardTheme = boardTheme,
                    modifier = Modifier.fillMaxSize()
                )
            }
            is RouteCell.Blocked -> {
                BlockedCellCanvas(
                    boardTheme = boardTheme,
                    modifier = Modifier.fillMaxSize()
                )
            }
            is RouteCell.Tile -> {
                RouteTileCanvas(
                    tile = cell.tile,
                    isHighlighted = isHighlighted,
                    isSelected = false,
                    boardTheme = boardTheme,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun EmptyCellCanvas(
    position: GridPosition,
    boardTheme: BoardThemeSetting,
    modifier: Modifier = Modifier
) {
    val bg = if (boardTheme == BoardThemeSetting.CLASSIC) CellEmptyClassic else CellEmptyMidnight
    val border = if (boardTheme == BoardThemeSetting.CLASSIC) CellEmptyBorderClassic else CellEmptyBorderMidnight

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .semantics {
                contentDescription = "Empty board cell, row ${position.row + 1}, column ${position.col + 1}"
            }
    ) {
        val w = size.width
        val h = size.height

        // Recessed cell fill
        drawRoundRect(
            color = bg,
            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f)
        )

        // Subtle recessed border
        drawRoundRect(
            color = border,
            style = Stroke(width = 1.dp.toPx()),
            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f)
        )

        // Subtle center alignment crosshair dot
        drawCircle(
            color = border.copy(alpha = 0.65f),
            radius = w * 0.045f,
            center = Offset(w / 2f, h / 2f)
        )
    }
}

@Composable
fun BlockedCellCanvas(
    boardTheme: BoardThemeSetting,
    modifier: Modifier = Modifier
) {
    val bg = if (boardTheme == BoardThemeSetting.CLASSIC) CellBlockedClassic else CellBlockedMidnight
    val hatchColor = if (boardTheme == BoardThemeSetting.CLASSIC) CellBlockedHatchClassic else CellBlockedHatchMidnight

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "Blocked cell" }
    ) {
        val w = size.width
        val h = size.height

        // Blocked stone body
        drawRoundRect(
            color = bg,
            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f)
        )

        // Diagonal hatch cross-lines (architectural obstruction hatch)
        val step = w * 0.28f
        val strokeWidth = 1.5.dp.toPx()

        var x = -w
        while (x < w * 2) {
            drawLine(
                color = hatchColor,
                start = Offset(x, 0f),
                end = Offset(x + h, h),
                strokeWidth = strokeWidth
            )
            x += step
        }

        // Inner shadow border
        drawRoundRect(
            color = bg.copy(alpha = 0.5f),
            style = Stroke(width = 2.dp.toPx()),
            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f)
        )
    }
}
