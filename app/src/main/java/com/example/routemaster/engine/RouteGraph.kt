package com.example.routemaster.engine

import com.example.routemaster.model.BoardConstants
import com.example.routemaster.model.Direction
import com.example.routemaster.model.GridPosition
import com.example.routemaster.model.RouteCell
import com.example.routemaster.model.RouteTile
import com.example.routemaster.model.opposite
import java.util.ArrayDeque

object RouteGraph {

    /**
     * Checks if current cell at [fromPos] connects to neighbor in [direction].
     */
    fun areConnected(
        board: List<List<RouteCell>>,
        fromPos: GridPosition,
        direction: Direction
    ): Boolean {
        if (!fromPos.isValid(board.size, board[0].size)) return false
        val fromCell = board[fromPos.row][fromPos.col] as? RouteCell.Tile ?: return false

        val neighborPos = fromPos.neighbor(direction)
        if (!neighborPos.isValid(board.size, board[0].size)) return false
        val neighborCell = board[neighborPos.row][neighborPos.col] as? RouteCell.Tile ?: return false

        val fromConnections = fromCell.tile.getConnections()
        val toConnections = neighborCell.tile.getConnections()

        val reqOpposite = opposite(direction)
        return direction in fromConnections && reqOpposite in toConnections
    }

    /**
     * Returns all direct adjacent neighbors connected by matching edge openings.
     */
    fun getConnectedNeighbors(
        board: List<List<RouteCell>>,
        position: GridPosition
    ): List<GridPosition> {
        val neighbors = mutableListOf<GridPosition>()
        for (direction in Direction.entries) {
            if (areConnected(board, position, direction)) {
                neighbors.add(position.neighbor(direction))
            }
        }
        return neighbors
    }

    /**
     * Finds the full connected component starting at [start] using BFS.
     * All traversed positions are added to [visited] so each tile is visited once.
     */
    fun getConnectedComponent(
        board: List<List<RouteCell>>,
        start: GridPosition,
        visited: MutableSet<GridPosition>
    ): Set<GridPosition> {
        val component = mutableSetOf<GridPosition>()
        val cell = board.getOrNull(start.row)?.getOrNull(start.col)
        if (cell !is RouteCell.Tile) return component

        val queue = ArrayDeque<GridPosition>()
        queue.add(start)
        visited.add(start)
        component.add(start)

        while (queue.isNotEmpty()) {
            val current = queue.poll() ?: break
            val neighbors = getConnectedNeighbors(board, current)
            for (neighbor in neighbors) {
                if (visited.add(neighbor)) {
                    component.add(neighbor)
                    queue.add(neighbor)
                }
            }
        }

        return component
    }

    /**
     * Iterates over every placed tile on the board, traverses all disjoint connected components,
     * and returns the largest connected network of tiles.
     */
    fun findLongestRoute(board: List<List<RouteCell>>): Set<GridPosition> {
        val visited = mutableSetOf<GridPosition>()
        var longestComponent: Set<GridPosition> = emptySet()

        for (r in board.indices) {
            for (c in board[r].indices) {
                val pos = GridPosition(r, c)
                if (board[r][c] is RouteCell.Tile && pos !in visited) {
                    val component = getConnectedComponent(board, pos, visited)
                    if (component.size > longestComponent.size) {
                        longestComponent = component
                    }
                }
            }
        }

        return longestComponent
    }

    /**
     * Returns all disjoint connected components on the board.
     */
    fun getAllComponents(board: List<List<RouteCell>>): List<Set<GridPosition>> {
        val visited = mutableSetOf<GridPosition>()
        val components = mutableListOf<Set<GridPosition>>()

        for (r in board.indices) {
            for (c in board[r].indices) {
                val pos = GridPosition(r, c)
                if (board[r][c] is RouteCell.Tile && pos !in visited) {
                    val comp = getConnectedComponent(board, pos, visited)
                    if (comp.isNotEmpty()) {
                        components.add(comp)
                    }
                }
            }
        }
        return components
    }

    /**
     * Counts how many tiles are placed on the board.
     */
    fun countPlacedTiles(board: List<List<RouteCell>>): Int {
        var count = 0
        for (r in board.indices) {
            for (c in board[r].indices) {
                if (board[r][c] is RouteCell.Tile) count++
            }
        }
        return count
    }

    /**
     * Counts how many empty playable cells remain on the board.
     */
    fun countEmptyPlayableCells(board: List<List<RouteCell>>): Int {
        var count = 0
        for (r in board.indices) {
            for (c in board[r].indices) {
                if (board[r][c] is RouteCell.Empty) count++
            }
        }
        return count
    }
}
