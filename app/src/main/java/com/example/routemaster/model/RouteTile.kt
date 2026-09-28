package com.example.routemaster.model

data class RouteTile(
    val id: Int,
    val kind: TileKind,
    val rotation: Int = 0 // 0, 90, 180, 270
) {
    fun rotateClockwise(): RouteTile {
        return copy(rotation = (rotation + 90) % 360)
    }

    fun getConnections(): Set<Direction> {
        return kind.baseConnections.map { it.rotatedClockwise(rotation) }.toSet()
    }
}

fun getConnections(tile: RouteTile): Set<Direction> = tile.getConnections()
