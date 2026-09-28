package com.example.routemaster.ui.game

import com.example.routemaster.model.BoardConstants
import com.example.routemaster.model.GridPosition
import com.example.routemaster.model.RouteCell
import com.example.routemaster.model.RouteTile

data class RouteMasterState(
    val board: List<List<RouteCell>> = BoardConstants.createInitialBoard(),
    val tray: List<RouteTile> = emptyList(),
    val bag: List<RouteTile> = emptyList(),
    val selectedTileId: Int? = null,
    val moveCount: Int = 0,
    val longestRouteLength: Int = 0,
    val longestRouteCells: Set<GridPosition> = emptySet(),
    val lastPlacedPosition: GridPosition? = null,
    val elapsedSeconds: Long = 0L,
    val isFinished: Boolean = false,
    val isAnimating: Boolean = false,
    val newRouteDelta: Int? = null
) {
    val totalPlacedTiles: Int
        get() = board.sumOf { row -> row.count { it is RouteCell.Tile } }

    val remainingPlayableCells: Int
        get() = board.sumOf { row -> row.count { it is RouteCell.Empty } }

    val tilesLeft: Int
        get() = tray.size + bag.size

    val disconnectedTiles: Int
        get() = maxOf(0, totalPlacedTiles - longestRouteLength)

    val boardCoveragePercent: Int
        get() = if (BoardConstants.TOTAL_PLAYABLE_CELLS > 0) {
            (totalPlacedTiles * 100) / BoardConstants.TOTAL_PLAYABLE_CELLS
        } else {
            0
        }
}
