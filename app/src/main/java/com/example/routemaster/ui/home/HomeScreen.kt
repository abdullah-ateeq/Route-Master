package com.example.routemaster.ui.home

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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.routemaster.data.GameStatistics
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
fun HomeScreen(
    statistics: GameStatistics,
    onPlayClick: () -> Unit,
    onHowToPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Graphite950)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Branding Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            // Minimal Geometric Network Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Graphite850)
                    .border(1.5.dp, AccentTeal.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "RM",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    letterSpacing = 2.sp,
                    color = AccentTeal
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "ROUTE MASTER",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                letterSpacing = 3.5.sp,
                color = TextWhitePrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "\"Build farther. Connect smarter.\"",
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp,
                color = TextGraySecondary
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Optional Stats Overview Card
        if (statistics.gamesPlayed > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Graphite850)
                    .border(1.dp, Graphite700, RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "CAREER PERFORMANCE",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 1.2.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        HomeStatColumn(
                            label = "GAMES",
                            value = "${statistics.gamesPlayed}"
                        )
                        HomeStatColumn(
                            label = "BEST ROUTE",
                            value = "${statistics.bestRouteLength}",
                            accent = TrackHighlightTeal
                        )
                        HomeStatColumn(
                            label = "BEST MOVES",
                            value = if (statistics.bestMoves > 0) "${statistics.bestMoves}" else "—"
                        )
                        val bestMin = statistics.bestTimeSeconds / 60
                        val bestSec = statistics.bestTimeSeconds % 60
                        HomeStatColumn(
                            label = "BEST TIME",
                            value = if (statistics.bestTimeSeconds > 0) String.format("%02d:%02d", bestMin, bestSec) else "—"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Primary Play Button
            Button(
                onClick = onPlayClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentTeal,
                    contentColor = BoardBgMidnight
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PLAY",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    letterSpacing = 2.sp
                )
            }

            // Secondary Buttons
            HomeMenuButton(
                title = "HOW TO PLAY",
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                onClick = onHowToPlayClick
            )

            HomeMenuButton(
                title = "SETTINGS",
                icon = Icons.Default.Settings,
                onClick = onSettingsClick
            )

            HomeMenuButton(
                title = "ABOUT",
                icon = Icons.Default.Info,
                onClick = onAboutClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "VERSION 1.0  •  OFFLINE ARCHITECTURAL PUZZLE",
            color = TextMuted,
            fontSize = 11.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun HomeStatColumn(
    label: String,
    value: String,
    accent: Color = TextWhitePrimary
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = accent
        )
    }
}

@Composable
private fun HomeMenuButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Graphite850,
            contentColor = TextWhitePrimary
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Graphite700)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextGraySecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            letterSpacing = 1.sp
        )
    }
}
