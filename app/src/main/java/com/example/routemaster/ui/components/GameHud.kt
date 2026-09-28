package com.example.routemaster.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.routemaster.ui.theme.AccentTeal
import com.example.routemaster.ui.theme.Graphite700
import com.example.routemaster.ui.theme.Graphite800
import com.example.routemaster.ui.theme.Graphite850
import com.example.routemaster.ui.theme.TextGraySecondary
import com.example.routemaster.ui.theme.TextMuted
import com.example.routemaster.ui.theme.TextWhitePrimary
import com.example.routemaster.ui.theme.TrackHighlightTeal

@Composable
fun GameHud(
    longestRoute: Int,
    tilesLeft: Int,
    moves: Int,
    elapsedSeconds: Long,
    showTimer: Boolean,
    newRouteDelta: Int?,
    onRestartClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar: Navigation & Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onRestartClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Restart game",
                    tint = TextGraySecondary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ROUTE MASTER",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 2.sp,
                    color = TextWhitePrimary
                )
                Text(
                    text = "6×6 NETWORK",
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = AccentTeal
                )
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = TextGraySecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Stats Card Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Graphite850)
                .border(1.dp, Graphite700, RoundedCornerShape(14.dp))
                .padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HudStatItem(
                label = "LONGEST ROUTE",
                value = longestRoute.toString(),
                accentColor = TrackHighlightTeal,
                modifier = Modifier.weight(1.3f)
            )

            HudDivider()

            HudStatItem(
                label = "TILES LEFT",
                value = tilesLeft.toString(),
                accentColor = TextWhitePrimary,
                modifier = Modifier.weight(1f)
            )

            HudDivider()

            HudStatItem(
                label = "MOVES",
                value = moves.toString(),
                accentColor = TextWhitePrimary,
                modifier = Modifier.weight(0.9f)
            )

            if (showTimer) {
                HudDivider()

                val minutes = elapsedSeconds / 60
                val seconds = elapsedSeconds % 60
                val formattedTime = String.format("%02d:%02d", minutes, seconds)

                HudStatItem(
                    label = "TIME",
                    value = formattedTime,
                    accentColor = TextGraySecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Restrained Animated Banner for "NEW LONGEST ROUTE (+N)"
        AnimatedVisibility(
            visible = newRouteDelta != null && newRouteDelta > 0,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentTeal.copy(alpha = 0.15f))
                    .border(1.dp, AccentTeal.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "NEW LONGEST ROUTE  +$newRouteDelta",
                    color = TrackHighlightTeal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun HudStatItem(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.6.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = accentColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun HudDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(24.dp)
            .background(Graphite700)
    )
}
