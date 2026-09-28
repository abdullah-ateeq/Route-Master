package com.example.routemaster.model

enum class TileKind(val displayName: String) {
    STRAIGHT("Straight"),
    CORNER("Corner"),
    T_JUNCTION("T-Junction"),
    CROSS("Cross");

    /**
     * Base connection directions before rotation (rotation = 0):
     * STRAIGHT: LEFT, RIGHT
     * CORNER: UP, RIGHT
     * T_JUNCTION: UP, LEFT, RIGHT
     * CROSS: UP, RIGHT, DOWN, LEFT
     */
    val baseConnections: Set<Direction>
        get() = when (this) {
            STRAIGHT -> setOf(Direction.LEFT, Direction.RIGHT)
            CORNER -> setOf(Direction.UP, Direction.RIGHT)
            T_JUNCTION -> setOf(Direction.UP, Direction.LEFT, Direction.RIGHT)
            CROSS -> setOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)
        }
}
