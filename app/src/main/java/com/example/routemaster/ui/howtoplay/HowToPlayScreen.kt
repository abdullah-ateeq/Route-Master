package com.example.routemaster.ui.howtoplay

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.routemaster.data.BoardThemeSetting
import com.example.routemaster.model.RouteTile
import com.example.routemaster.model.TileKind
import com.example.routemaster.ui.components.RouteTileCanvas
import com.example.routemaster.ui.theme.AccentAmber
import com.example.routemaster.ui.theme.AccentRose
import com.example.routemaster.ui.theme.AccentTeal
import com.example.routemaster.ui.theme.Graphite700
import com.example.routemaster.ui.theme.Graphite800
import com.example.routemaster.ui.theme.Graphite850
import com.example.routemaster.ui.theme.Graphite950
import com.example.routemaster.ui.theme.TextGraySecondary
import com.example.routemaster.ui.theme.TextMuted
import com.example.routemaster.ui.theme.TextWhitePrimary
import com.example.routemaster.ui.theme.TrackHighlightTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowToPlayScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "HOW TO PLAY",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 2.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhitePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Graphite950,
                    titleContentColor = TextWhitePrimary
                )
            )
        },
        containerColor = Graphite950,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Objective Card
            RuleSectionCard(
                title = "OBJECTIVE",
                description = "Place and rotate modular route tiles onto a compact 6×6 board to construct the longest continuous connected network. When your tile supply runs out, the number of tiles in your best connected network is your final score."
            )

            // Tile Types Showcase
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Graphite850)
                    .border(1.dp, Graphite700, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "THE FOUR ROUTE TILES",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.2.sp,
                    color = AccentTeal
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TileShowcaseItem(
                        tile = RouteTile(1, TileKind.STRAIGHT, 0),
                        name = "Straight",
                        sides = "Left + Right"
                    )
                    TileShowcaseItem(
                        tile = RouteTile(2, TileKind.CORNER, 0),
                        name = "Corner",
                        sides = "Up + Right"
                    )
                    TileShowcaseItem(
                        tile = RouteTile(3, TileKind.T_JUNCTION, 0),
                        name = "T-Junction",
                        sides = "Up, Left, Right"
                    )
                    TileShowcaseItem(
                        tile = RouteTile(4, TileKind.CROSS, 0),
                        name = "Cross",
                        sides = "All 4 sides"
                    )
                }
            }

            // Connection Matching Rules & Visual Example
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Graphite850)
                    .border(1.dp, Graphite700, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "CONNECTION RULES",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.2.sp,
                    color = AccentTeal
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Two adjacent tiles connect ONLY if the current tile has an opening facing the neighbor AND the neighbor has an opening facing back.",
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = TextGraySecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Connection Examples
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Valid connection example
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RouteTileCanvas(
                                tile = RouteTile(10, TileKind.STRAIGHT, 0),
                                isHighlighted = true,
                                boardTheme = BoardThemeSetting.MIDNIGHT,
                                modifier = Modifier.size(46.dp)
                            )
                            RouteTileCanvas(
                                tile = RouteTile(11, TileKind.STRAIGHT, 0),
                                isHighlighted = true,
                                boardTheme = BoardThemeSetting.MIDNIGHT,
                                modifier = Modifier.size(46.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = AccentTeal,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Connected",
                                color = TrackHighlightTeal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Mismatched example
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RouteTileCanvas(
                                tile = RouteTile(12, TileKind.STRAIGHT, 0),
                                isHighlighted = false,
                                boardTheme = BoardThemeSetting.MIDNIGHT,
                                modifier = Modifier.size(46.dp)
                            )
                            RouteTileCanvas(
                                tile = RouteTile(13, TileKind.STRAIGHT, 90),
                                isHighlighted = false,
                                boardTheme = BoardThemeSetting.MIDNIGHT,
                                modifier = Modifier.size(46.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = AccentRose,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Mismatched",
                                color = AccentRose,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Core Gameplay Steps
            RuleSectionCard(
                title = "GAMEPLAY FLOW",
                description = "1. Select a tile from the 3 in your tray.\n" +
                        "2. Tap ROTATE to turn it 90° clockwise.\n" +
                        "3. Tap any empty playable grid cell to place it.\n" +
                        "4. A replacement is drawn from the bag (18 tiles total).\n" +
                        "5. If you make a mistake, tap UNDO to revert your last move."
            )

            // Scoring Card
            RuleSectionCard(
                title = "SCORING & LONGEST ROUTE",
                description = "Your score equals the total number of connected tiles in your single largest connected network.\n\n" +
                        "• Branches, loops, T-junctions, and Crosses all count!\n" +
                        "• Each connected tile counts exactly once.\n" +
                        "• Disconnected tiles do not penalize you; they simply do not add to your highest route.\n" +
                        "• Look out for the 4 fixed blocked cells on the board!"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RuleSectionCard(
    title: String,
    description: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Graphite850)
            .border(1.dp, Graphite700, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.2.sp,
                color = AccentTeal
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = TextGraySecondary
            )
        }
    }
}

@Composable
private fun TileShowcaseItem(
    tile: RouteTile,
    name: String,
    sides: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(68.dp)
    ) {
        RouteTileCanvas(
            tile = tile,
            isHighlighted = false,
            boardTheme = BoardThemeSetting.MIDNIGHT,
            modifier = Modifier.size(52.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhitePrimary
        )
        Text(
            text = sides,
            fontSize = 9.sp,
            color = TextMuted,
            lineHeight = 12.sp
        )
    }
}
