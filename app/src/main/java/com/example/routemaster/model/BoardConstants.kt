package com.example.routemaster.model

object BoardConstants {
    const val GRID_SIZE = 6
    const val TOTAL_CELLS = GRID_SIZE * GRID_SIZE

    /**
     * Symmetrical 4 blocked positions in the 6x6 grid.
     */
    val FIXED_BLOCKED_CELLS: Set<GridPosition> = setOf(
        GridPosition(1, 1),
        GridPosition(1, 4),
        GridPosition(4, 1),
        GridPosition(4, 4)
    )

    val TOTAL_BLOCKED_CELLS = FIXED_BLOCKED_CELLS.size
    val TOTAL_PLAYABLE_CELLS = TOTAL_CELLS - TOTAL_BLOCKED_CELLS

    fun createInitialBoard(): List<List<RouteCell>> {
        return List(GRID_SIZE) { r ->
            List(GRID_SIZE) { c ->
                val pos = GridPosition(r, c)
                if (pos in FIXED_BLOCKED_CELLS) {
                    RouteCell.Blocked
                } else {
                    RouteCell.Empty
                }
            }
        }
    }
}
