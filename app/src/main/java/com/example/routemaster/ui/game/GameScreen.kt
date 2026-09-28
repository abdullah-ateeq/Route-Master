package com.example.routemaster.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.routemaster.data.BoardThemeSetting
import com.example.routemaster.ui.components.GameHud
import com.example.routemaster.ui.components.RouteBoard
import com.example.routemaster.ui.components.TileTrayView
import com.example.routemaster.ui.theme.AccentTeal
import com.example.routemaster.ui.theme.BoardBgMidnight
import com.example.routemaster.ui.theme.Graphite800
import com.example.routemaster.ui.theme.Graphite850
import com.example.routemaster.ui.theme.Graphite950
import com.example.routemaster.ui.theme.TextGraySecondary
import com.example.routemaster.ui.theme.TextMuted
import com.example.routemaster.ui.theme.TextWhitePrimary

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateToResult: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.gameState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    var showRestartDialog by remember { mutableStateOf(false) }

    // When game finishes, trigger navigation to Result screen
    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            onNavigateToResult()
        }
    }

    if (showRestartDialog) {
        AlertDialog(
            onDismissRequest = { showRestartDialog = false },
            containerColor = Graphite850,
            titleContentColor = TextWhitePrimary,
            textContentColor = TextGraySecondary,
            title = {
                Text(
                    text = "Restart Game?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "This will restore the original board layout and tile bag for this match.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRestartDialog = false
                        viewModel.restartGame()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = AccentTeal)
                ) {
                    Text("Restart", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRestartDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Graphite950)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isLandscape = maxWidth > maxHeight

            if (isLandscape) {
                // Landscape / Tablet Split Layout
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Board on Left
                    Box(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        RouteBoard(
                            board = state.board,
                            longestRouteCells = state.longestRouteCells,
                            highlightEnabled = settings.highlightLongestRoute,
                            boardTheme = settings.boardTheme,
                            lastPlacedPosition = state.lastPlacedPosition,
                            onCellClick = { pos -> viewModel.placeTile(pos) },
                            modifier = Modifier.aspectRatio(1f)
                        )
                    }

                    // HUD & Controls on Right
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GameHud(
                            longestRoute = state.longestRouteLength,
                            tilesLeft = state.tilesLeft,
                            moves = state.moveCount,
                            elapsedSeconds = state.elapsedSeconds,
                            showTimer = settings.showTimer,
                            newRouteDelta = state.newRouteDelta,
                            onRestartClick = { showRestartDialog = true },
                            onSettingsClick = onNavigateToSettings
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TileTrayView(
                            trayTiles = state.tray,
                            selectedTileId = state.selectedTileId,
                            boardTheme = settings.boardTheme,
                            canUndo = viewModel.canUndo,
                            isInputLocked = state.isAnimating || state.isFinished,
                            onSelectTile = { tile -> viewModel.selectTile(tile) },
                            onRotateClick = { viewModel.rotateSelectedTile() },
                            onUndoClick = { viewModel.undo() }
                        )
                    }
                }
            } else {
                // Portrait Layout
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GameHud(
                        longestRoute = state.longestRouteLength,
                        tilesLeft = state.tilesLeft,
                        moves = state.moveCount,
                        elapsedSeconds = state.elapsedSeconds,
                        showTimer = settings.showTimer,
                        newRouteDelta = state.newRouteDelta,
                        onRestartClick = { showRestartDialog = true },
                        onSettingsClick = onNavigateToSettings
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Center 6x6 Board
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        RouteBoard(
                            board = state.board,
                            longestRouteCells = state.longestRouteCells,
                            highlightEnabled = settings.highlightLongestRoute,
                            boardTheme = settings.boardTheme,
                            lastPlacedPosition = state.lastPlacedPosition,
                            onCellClick = { pos -> viewModel.placeTile(pos) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tile Tray and Action Controls
                    TileTrayView(
                        trayTiles = state.tray,
                        selectedTileId = state.selectedTileId,
                        boardTheme = settings.boardTheme,
                        canUndo = viewModel.canUndo,
                        isInputLocked = state.isAnimating || state.isFinished,
                        onSelectTile = { tile -> viewModel.selectTile(tile) },
                        onRotateClick = { viewModel.rotateSelectedTile() },
                        onUndoClick = { viewModel.undo() }
                    )
                }
            }
        }
    }
}
