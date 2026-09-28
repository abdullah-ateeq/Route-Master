package com.example.routemaster.ui.result

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.routemaster.ui.game.RouteMasterState
import com.example.routemaster.ui.theme.AccentTeal
import com.example.routemaster.ui.theme.BoardBgMidnight
import com.example.routemaster.ui.theme.Graphite700
import com.example.routemaster.ui.theme.Graphite800
import com.example.routemaster.ui.theme.Graphite850
import com.example.routemaster.ui.theme.Graphite950
import com.example.routemaster.ui.theme.TextGraySecondary
import com.example.routemaster.ui.theme.TextMuted
import com.example.routemaster.ui.theme.TextWhitePrimary
import com.example.routemaster.ui.theme.TrackHighlightTeal

@Composable
fun ResultScreen(
    state: RouteMasterState,
    onPlayAgainClick: () -> Unit,
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val durationMinutes = state.elapsedSeconds / 60
    val durationSeconds = state.elapsedSeconds % 60
    val formattedDuration = String.format("%02d:%02d", durationMinutes, durationSeconds)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Graphite950)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "MATCH CONCLUDED",
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                color = AccentTeal
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "ROUTE COMPLETE",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                letterSpacing = 3.sp,
                color = TextWhitePrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Prominent Longest Route Hero Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Graphite850)
                    .border(1.5.dp, AccentTeal.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(vertical = 24.dp, horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "LONGEST ROUTE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 2.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${state.longestRouteLength}",
                        fontWeight = FontWeight.Black,
                        fontSize = 64.sp,
                        letterSpacing = 1.sp,
                        color = TrackHighlightTeal
                    )

                    Text(
                        text = "CONNECTED TILES",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        letterSpacing = 1.5.sp,
                        color = TextGraySecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Performance Statistics Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Graphite850)
                    .border(1.dp, Graphite700, RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    ResultStatRow(
                        label = "Tiles Placed",
                        value = "${state.totalPlacedTiles}"
                    )
                    ResultStatRow(
                        label = "Moves",
                        value = "${state.moveCount}"
                    )
                    ResultStatRow(
                        label = "Disconnected Tiles",
                        value = "${state.disconnectedTiles}",
                        valueColor = if (state.disconnectedTiles == 0) AccentTeal else TextWhitePrimary
                    )
                    ResultStatRow(
                        label = "Board Coverage",
                        value = "${state.boardCoveragePercent}%"
                    )
                    ResultStatRow(
                        label = "Match Duration",
                        value = formattedDuration
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Navigation Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onPlayAgainClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentTeal,
                    contentColor = BoardBgMidnight
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PLAY AGAIN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 1.5.sp
                )
            }

            OutlinedButton(
                onClick = onHomeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Graphite850,
                    contentColor = TextWhitePrimary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Graphite700)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = TextGraySecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "HOME",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun ResultStatRow(
    label: String,
    value: String,
    valueColor: Color = TextWhitePrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextGraySecondary,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
    }
}
