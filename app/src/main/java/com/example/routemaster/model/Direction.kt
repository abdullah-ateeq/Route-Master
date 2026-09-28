package com.example.routemaster.model

enum class Direction(val dRow: Int, val dCol: Int) {
    UP(-1, 0),
    RIGHT(0, 1),
    DOWN(1, 0),
    LEFT(0, -1);

    fun opposite(): Direction = when (this) {
        UP -> DOWN
        RIGHT -> LEFT
        DOWN -> UP
        LEFT -> RIGHT
    }

    fun rotatedClockwise(degrees: Int): Direction {
        val steps = ((degrees % 360) / 90)
        val newOrdinal = (this.ordinal + steps).mod(4)
        return entries[newOrdinal]
    }
}

fun opposite(direction: Direction): Direction = direction.opposite()
