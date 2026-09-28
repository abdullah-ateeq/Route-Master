package com.example.routemaster.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.routemaster.ui.theme.AccentTeal
import com.example.routemaster.ui.theme.BoardBgMidnight
import com.example.routemaster.ui.theme.BoardBorderMidnight
import com.example.routemaster.ui.theme.CellEmptyMidnight
import com.example.routemaster.ui.theme.Graphite850
import com.example.routemaster.ui.theme.Graphite950
import com.example.routemaster.ui.theme.TextGraySecondary
import com.example.routemaster.ui.theme.TextWhitePrimary
import com.example.routemaster.ui.theme.TrackBronze
import com.example.routemaster.ui.theme.TrackHighlightBorder
import com.example.routemaster.ui.theme.TrackHighlightGlow
import com.example.routemaster.ui.theme.TrackHighlightTeal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val gridAlpha = remember { Animatable(0f) }
    val tilesAlpha = remember { Animatable(0f) }
    val networkProgress = remember { Animatable(0f) }
    val highlightAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Sequence timed for ~1.5s total duration
        launch {
            gridAlpha.animateTo(1f, animationSpec = tween(350, easing = LinearEasing))
        }
        delay(250)
        launch {
            tilesAlpha.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
        delay(350)
        launch {
            networkProgress.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        }
        delay(300)
        launch {
            highlightAlpha.animateTo(1f, animationSpec = tween(350, easing = FastOutSlowInEasing))
        }
        launch {
            titleAlpha.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
        delay(550)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Graphite950),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Mini Grid with forming route network
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .alpha(gridAlpha.value),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val cellSize = w / 3f
                    val pad = cellSize * 0.08f
                    val innerSize = cellSize - pad * 2

                    // 1. Draw 3x3 base grid
                    for (r in 0 until 3) {
                        for (c in 0 until 3) {
                            val ox = c * cellSize + pad
                            val oy = r * cellSize + pad
                            drawRoundRect(
                                color = CellEmptyMidnight,
                                topLeft = Offset(ox, oy),
                                size = Size(innerSize, innerSize),
                                cornerRadius = CornerRadius(cellSize * 0.15f, cellSize * 0.15f)
                            )
                            drawRoundRect(
                                color = BoardBorderMidnight,
                                topLeft = Offset(ox, oy),
                                size = Size(innerSize, innerSize),
                                cornerRadius = CornerRadius(cellSize * 0.15f, cellSize * 0.15f),
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }
                    }

                    // 2. Draw route tiles in an S-curve path:
                    // (0,0) Corner [Right, Down] -> (0,1) Straight [Left, Right] -> (0,2) Corner [Left, Down]
                    // -> (1,2) Corner [Up, Left] -> (1,1) Straight [Right, Left] -> (1,0) Corner [Right, Down]
                    // -> (2,0) Corner [Up, Right] -> (2,1) Straight [Left, Right] -> (2,2) Cross
                    val tAlpha = tilesAlpha.value
                    if (tAlpha > 0f) {
                        val pathPoints = listOf(
                            Offset(0.5f * cellSize, 0.5f * cellSize),
                            Offset(1.5f * cellSize, 0.5f * cellSize),
                            Offset(2.5f * cellSize, 0.5f * cellSize),
                            Offset(2.5f * cellSize, 1.5f * cellSize),
                            Offset(1.5f * cellSize, 1.5f * cellSize),
                            Offset(0.5f * cellSize, 1.5f * cellSize),
                            Offset(0.5f * cellSize, 2.5f * cellSize),
                            Offset(1.5f * cellSize, 2.5f * cellSize),
                            Offset(2.5f * cellSize, 2.5f * cellSize)
                        )

                        // Render tile bodies
                        for (p in pathPoints) {
                            val ox = p.x - innerSize / 2f
                            val oy = p.y - innerSize / 2f
                            drawRoundRect(
                                color = Graphite850.copy(alpha = tAlpha),
                                topLeft = Offset(ox, oy),
                                size = Size(innerSize, innerSize),
                                cornerRadius = CornerRadius(cellSize * 0.15f, cellSize * 0.15f)
                            )
                        }

                        // 3. Connect line segments progressively
                        val progress = networkProgress.value
                        val hlAlpha = highlightAlpha.value

                        val totalSegments = pathPoints.size - 1
                        for (i in 0 until totalSegments) {
                            val segProgress = ((progress * totalSegments) - i).coerceIn(0f, 1f)
                            if (segProgress > 0f) {
                                val p1 = pathPoints[i]
                                val p2 = pathPoints[i + 1]
                                val currentEnd = Offset(
                                    p1.x + (p2.x - p1.x) * segProgress,
                                    p1.y + (p2.y - p1.y) * segProgress
                                )

                                // Base track
                                drawLine(
                                    color = TrackBronze.copy(alpha = tAlpha),
                                    start = p1,
                                    end = currentEnd,
                                    strokeWidth = cellSize * 0.2f,
                                    cap = StrokeCap.Round
                                )

                                // Highlight track overlay
                                if (hlAlpha > 0f) {
                                    drawLine(
                                        color = TrackHighlightGlow.copy(alpha = hlAlpha * 0.6f),
                                        start = p1,
                                        end = currentEnd,
                                        strokeWidth = cellSize * 0.32f,
                                        cap = StrokeCap.Round
                                    )
                                    drawLine(
                                        color = TrackHighlightTeal.copy(alpha = hlAlpha),
                                        start = p1,
                                        end = currentEnd,
                                        strokeWidth = cellSize * 0.18f,
                                        cap = StrokeCap.Round
                                    )
                                }
                            }
                        }

                        // Junction nodes
                        for (p in pathPoints) {
                            drawCircle(
                                color = if (hlAlpha > 0f) TrackHighlightTeal else TrackBronze,
                                radius = cellSize * 0.1f,
                                center = p
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Title & Subtitle fading in
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(titleAlpha.value)
            ) {
                Text(
                    text = "ROUTE MASTER",
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    letterSpacing = 4.sp,
                    color = TextWhitePrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ARCHITECTURAL PUZZLE",
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    color = AccentTeal
                )
            }
        }
    }
}
