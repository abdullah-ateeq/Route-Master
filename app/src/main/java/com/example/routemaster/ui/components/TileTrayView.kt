package com.example.routemaster.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.routemaster.data.BoardThemeSetting
import com.example.routemaster.engine.TileBag
import com.example.routemaster.model.RouteTile
import com.example.routemaster.ui.theme.AccentTeal
import com.example.routemaster.ui.theme.BoardBgMidnight
import com.example.routemaster.ui.theme.Graphite700
import com.example.routemaster.ui.theme.Graphite800
import com.example.routemaster.ui.theme.Graphite850
import com.example.routemaster.ui.theme.TextGraySecondary
import com.example.routemaster.ui.theme.TextMuted
import com.example.routemaster.ui.theme.TextWhitePrimary
import com.example.routemaster.ui.theme.TileBorderActive

@Composable
fun TileTrayView(
    trayTiles: List<RouteTile>,
    selectedTileId: Int?,
    boardTheme: BoardThemeSetting,
    canUndo: Boolean,
    isInputLocked: Boolean,
    onSelectTile: (RouteTile) -> Unit,
    onRotateClick: () -> Unit,
    onUndoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tray Box containing up to 3 slots
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Graphite850)
                .border(1.5.dp, Graphite700, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (index in 0 until TileBag.TRAY_SIZE) {
                    val tile = trayTiles.getOrNull(index)
                    val isSelected = tile != null && tile.id == selectedTileId

                    val liftOffset by animateDpAsState(
                        targetValue = if (isSelected) (-8).dp else 0.dp,
                        animationSpec = tween(120),
                        label = "tileLift"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 6.dp)
                            .offset(y = liftOffset),
                        contentAlignment = Alignment.Center
                    ) {
                        if (tile != null) {
                            RouteTileCanvas(
                                tile = tile,
                                isHighlighted = false,
                                isSelected = isSelected,
                                boardTheme = boardTheme,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable(
                                        enabled = !isInputLocked,
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { onSelectTile(tile) }
                                    )
                            )
                        } else {
                            // Empty tray slot placeholder
                            EmptyTraySlot(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons Row: Rotate & Undo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rotate Button
            val isRotateEnabled = selectedTileId != null && !isInputLocked
            Button(
                onClick = onRotateClick,
                enabled = isRotateEnabled,
                modifier = Modifier
                    .weight(1.2f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentTeal,
                    contentColor = BoardBgMidnight,
                    disabledContainerColor = Graphite800,
                    disabledContentColor = TextMuted
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.RotateRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ROTATE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )
            }

            // Undo Button
            val isUndoActive = canUndo && !isInputLocked
            OutlinedButton(
                onClick = onUndoClick,
                enabled = isUndoActive,
                modifier = Modifier
                    .weight(0.9f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = TextWhitePrimary,
                    disabledContentColor = TextMuted
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isUndoActive) Graphite700 else Graphite800
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "UNDO",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun EmptyTraySlot(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Graphite800.copy(alpha = 0.4f))
            .border(1.dp, Graphite700.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "—",
            color = TextMuted.copy(alpha = 0.4f),
            fontSize = 16.sp
        )
    }
}
