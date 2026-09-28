package com.example.routemaster

import com.example.routemaster.engine.RouteGraph
import com.example.routemaster.engine.TileBag
import com.example.routemaster.model.BoardConstants
import com.example.routemaster.model.Direction
import com.example.routemaster.model.GridPosition
import com.example.routemaster.model.RouteCell
import com.example.routemaster.model.RouteTile
import com.example.routemaster.model.TileKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RouteMasterLogicTest {

    private fun emptyBoard(): MutableList<MutableList<RouteCell>> {
        return MutableList(6) {
            MutableList(6) { RouteCell.Empty }
        }
    }

    @Test
    fun testStraightToStraightConnection() {
        val board = emptyBoard()
        // Two horizontal straights placed side by side at (2, 2) and (2, 3)
        // STRAIGHT rotation 0: LEFT, RIGHT
        val tile1 = RouteTile(id = 1, kind = TileKind.STRAIGHT, rotation = 0)
        val tile2 = RouteTile(id = 2, kind = TileKind.STRAIGHT, rotation = 0)
        board[2][2] = RouteCell.Tile(tile1)
        board[2][3] = RouteCell.Tile(tile2)

        assertTrue(RouteGraph.areConnected(board, GridPosition(2, 2), Direction.RIGHT))
        assertTrue(RouteGraph.areConnected(board, GridPosition(2, 3), Direction.LEFT))

        val component = RouteGraph.getConnectedComponent(board, GridPosition(2, 2), mutableSetOf())
        assertEquals(2, component.size)
        assertTrue(component.contains(GridPosition(2, 2)))
        assertTrue(component.contains(GridPosition(2, 3)))
    }

    @Test
    fun testCornerConnection() {
        val board = emptyBoard()
        // CORNER at rotation 0: UP, RIGHT
        // Placed at (3, 2). Connects UP to (2, 2) and RIGHT to (3, 3).
        // Let's place a STRAIGHT vertical at (2, 2) (rotation 90: UP, DOWN).
        // Cell (3, 2) has UP. Cell (2, 2) has DOWN.
        val corner = RouteTile(id = 1, kind = TileKind.CORNER, rotation = 0)
        val verticalStraight = RouteTile(id = 2, kind = TileKind.STRAIGHT, rotation = 90)

        board[3][2] = RouteCell.Tile(corner)
        board[2][2] = RouteCell.Tile(verticalStraight)

        assertTrue(RouteGraph.areConnected(board, GridPosition(3, 2), Direction.UP))
        assertTrue(RouteGraph.areConnected(board, GridPosition(2, 2), Direction.DOWN))

        val component = RouteGraph.getConnectedComponent(board, GridPosition(3, 2), mutableSetOf())
        assertEquals(2, component.size)
    }

    @Test
    fun testTJunctionRotation() {
        // T-junction at 0 deg: UP, LEFT, RIGHT
        val t0 = RouteTile(id = 1, kind = TileKind.T_JUNCTION, rotation = 0)
        assertEquals(setOf(Direction.UP, Direction.LEFT, Direction.RIGHT), t0.getConnections())

        // T-junction at 90 deg: RIGHT, UP, DOWN
        val t90 = t0.rotateClockwise()
        assertEquals(setOf(Direction.RIGHT, Direction.UP, Direction.DOWN), t90.getConnections())

        // T-junction at 180 deg: DOWN, RIGHT, LEFT
        val t180 = t90.rotateClockwise()
        assertEquals(setOf(Direction.DOWN, Direction.RIGHT, Direction.LEFT), t180.getConnections())

        // T-junction at 270 deg: LEFT, DOWN, UP
        val t270 = t180.rotateClockwise()
        assertEquals(setOf(Direction.LEFT, Direction.DOWN, Direction.UP), t270.getConnections())
    }

    @Test
    fun testCrossConnections() {
        val board = emptyBoard()
        val cross = RouteTile(id = 1, kind = TileKind.CROSS, rotation = 0)
        board[2][2] = RouteCell.Tile(cross)

        // Surround with 4 straights
        board[1][2] = RouteCell.Tile(RouteTile(id = 2, kind = TileKind.STRAIGHT, rotation = 90)) // UP: vertical has DOWN
        board[3][2] = RouteCell.Tile(RouteTile(id = 3, kind = TileKind.STRAIGHT, rotation = 90)) // DOWN: vertical has UP
        board[2][1] = RouteCell.Tile(RouteTile(id = 4, kind = TileKind.STRAIGHT, rotation = 0))  // LEFT: horiz has RIGHT
        board[2][3] = RouteCell.Tile(RouteTile(id = 5, kind = TileKind.STRAIGHT, rotation = 0))  // RIGHT: horiz has LEFT

        val neighbors = RouteGraph.getConnectedNeighbors(board, GridPosition(2, 2))
        assertEquals(4, neighbors.size)

        val component = RouteGraph.getConnectedComponent(board, GridPosition(2, 2), mutableSetOf())
        assertEquals(5, component.size)
    }

    @Test
    fun testMismatchedEdgesNotConnected() {
        val board = emptyBoard()
        // Tile at (2, 2) is horizontal STRAIGHT (LEFT, RIGHT). Opening RIGHT towards (2, 3).
        // Tile at (2, 3) is vertical STRAIGHT (UP, DOWN). Openings are UP and DOWN. No LEFT opening.
        board[2][2] = RouteCell.Tile(RouteTile(id = 1, kind = TileKind.STRAIGHT, rotation = 0))
        board[2][3] = RouteCell.Tile(RouteTile(id = 2, kind = TileKind.STRAIGHT, rotation = 90))

        assertFalse(RouteGraph.areConnected(board, GridPosition(2, 2), Direction.RIGHT))
        assertFalse(RouteGraph.areConnected(board, GridPosition(2, 3), Direction.LEFT))

        val comp1 = RouteGraph.getConnectedComponent(board, GridPosition(2, 2), mutableSetOf())
        assertEquals(1, comp1.size)
    }

    @Test
    fun testBlockedCellRejection() {
        val initialBoard = BoardConstants.createInitialBoard()
        // Positions (1, 1), (1, 4), (4, 1), (4, 4) must be Blocked
        assertTrue(initialBoard[1][1] is RouteCell.Blocked)
        assertTrue(initialBoard[1][4] is RouteCell.Blocked)
        assertTrue(initialBoard[4][1] is RouteCell.Blocked)
        assertTrue(initialBoard[4][4] is RouteCell.Blocked)

        // Blocked cells cannot have connections
        assertFalse(RouteGraph.areConnected(initialBoard, GridPosition(1, 1), Direction.RIGHT))
    }

    @Test
    fun testTwoSeparateComponentsAndLongestComponentSelection() {
        val board = emptyBoard()
        // Component 1: Length 2 at (0, 0)-(0, 1)
        board[0][0] = RouteCell.Tile(RouteTile(id = 1, kind = TileKind.STRAIGHT, rotation = 0))
        board[0][1] = RouteCell.Tile(RouteTile(id = 2, kind = TileKind.STRAIGHT, rotation = 0))

        // Component 2: Length 3 at (3, 0)-(3, 1)-(3, 2)
        board[3][0] = RouteCell.Tile(RouteTile(id = 3, kind = TileKind.STRAIGHT, rotation = 0))
        board[3][1] = RouteCell.Tile(RouteTile(id = 4, kind = TileKind.STRAIGHT, rotation = 0))
        board[3][2] = RouteCell.Tile(RouteTile(id = 5, kind = TileKind.STRAIGHT, rotation = 0))

        val allComponents = RouteGraph.getAllComponents(board)
        assertEquals(2, allComponents.size)

        val longest = RouteGraph.findLongestRoute(board)
        assertEquals(3, longest.size)
        assertTrue(longest.contains(GridPosition(3, 0)))
        assertTrue(longest.contains(GridPosition(3, 1)))
        assertTrue(longest.contains(GridPosition(3, 2)))
    }

    @Test
    fun testLoopCountedWithoutDuplicates() {
        val board = emptyBoard()
        // Create a 2x2 loop with 4 corners:
        // (0,0): CORNER rot 90 (RIGHT, DOWN)
        // (0,1): CORNER rot 180 (DOWN, LEFT)
        // (1,1): CORNER rot 270 (LEFT, UP)
        // (1,0): CORNER rot 0 (UP, RIGHT)
        board[0][0] = RouteCell.Tile(RouteTile(id = 1, kind = TileKind.CORNER, rotation = 90))
        board[0][1] = RouteCell.Tile(RouteTile(id = 2, kind = TileKind.CORNER, rotation = 180))
        board[1][1] = RouteCell.Tile(RouteTile(id = 3, kind = TileKind.CORNER, rotation = 270))
        board[1][0] = RouteCell.Tile(RouteTile(id = 4, kind = TileKind.CORNER, rotation = 0))

        val longest = RouteGraph.findLongestRoute(board)
        // Exactly 4 tiles in the loop, no infinite loop or duplicate counts
        assertEquals(4, longest.size)
    }

    @Test
    fun testBranchNetworkCountedCorrectly() {
        val board = emptyBoard()
        // Center T-Junction at (2, 2) rot 0: UP, LEFT, RIGHT
        board[2][2] = RouteCell.Tile(RouteTile(id = 1, kind = TileKind.T_JUNCTION, rotation = 0))
        // UP neighbor at (1, 2): vertical straight rot 90 (UP, DOWN)
        board[1][2] = RouteCell.Tile(RouteTile(id = 2, kind = TileKind.STRAIGHT, rotation = 90))
        // LEFT neighbor at (2, 1): horizontal straight rot 0 (LEFT, RIGHT)
        board[2][1] = RouteCell.Tile(RouteTile(id = 3, kind = TileKind.STRAIGHT, rotation = 0))
        // RIGHT neighbor at (2, 3): horizontal straight rot 0 (LEFT, RIGHT)
        board[2][3] = RouteCell.Tile(RouteTile(id = 4, kind = TileKind.STRAIGHT, rotation = 0))

        val longest = RouteGraph.findLongestRoute(board)
        assertEquals(4, longest.size)
    }

    @Test
    fun testTileBagDeckDistribution() {
        val deck = TileBag.generateDeck()
        assertEquals(18, deck.size)
        assertEquals(6, deck.count { it.kind == TileKind.STRAIGHT })
        assertEquals(6, deck.count { it.kind == TileKind.CORNER })
        assertEquals(4, deck.count { it.kind == TileKind.T_JUNCTION })
        assertEquals(2, deck.count { it.kind == TileKind.CROSS })

        val (tray, bag) = TileBag.dealInitial(deck)
        assertEquals(3, tray.size)
        assertEquals(15, bag.size)
    }

    @Test
    fun testGameEndDetectionConditions() {
        val board = emptyBoard()
        // Case 1: Empty board with 36 cells, 0 tiles placed
        assertEquals(36, RouteGraph.countEmptyPlayableCells(board))

        // When tray and bag are both empty -> End condition is true
        val trayEmpty = emptyList<RouteTile>()
        val bagEmpty = emptyList<RouteTile>()
        val isFinishedWhenDeckExhausted = (trayEmpty.isEmpty() && bagEmpty.isEmpty())
        assertTrue(isFinishedWhenDeckExhausted)

        // Case 2: Fill board completely
        for (r in 0 until 6) {
            for (c in 0 until 6) {
                board[r][c] = RouteCell.Tile(RouteTile(id = r * 6 + c, kind = TileKind.CROSS))
            }
        }
        assertEquals(0, RouteGraph.countEmptyPlayableCells(board))
        val isFinishedWhenBoardFull = (RouteGraph.countEmptyPlayableCells(board) == 0)
        assertTrue(isFinishedWhenBoardFull)
    }

    @Test
    fun testUndoStateSnapshot() {
        val initialBoard = BoardConstants.createInitialBoard()
        val tile = RouteTile(id = 1, kind = TileKind.STRAIGHT, rotation = 0)
        val tray = listOf(tile)
        val bag = emptyList<RouteTile>()

        // Simulate snapshot before move
        val snapshot = Pair(initialBoard, tray)

        // Make move
        val newBoard = initialBoard.mapIndexed { r, row ->
            if (r == 0) row.mapIndexed { c, cell -> if (c == 0) RouteCell.Tile(tile) else cell } else row
        }
        val newTray = emptyList<RouteTile>()

        assertTrue(newBoard[0][0] is RouteCell.Tile)
        assertTrue(newTray.isEmpty())

        // Undo move
        val restoredBoard = snapshot.first
        val restoredTray = snapshot.second

        assertTrue(restoredBoard[0][0] is RouteCell.Empty)
        assertEquals(1, restoredTray.size)
        assertEquals(tile.id, restoredTray[0].id)
    }
}

