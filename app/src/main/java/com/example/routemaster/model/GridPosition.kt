package com.example.routemaster.model

data class GridPosition(
    val row: Int,
    val col: Int
) {
    fun neighbor(direction: Direction): GridPosition {
        return GridPosition(row + direction.dRow, col + direction.dCol)
    }

    fun isValid(numRows: Int = 6, numCols: Int = 6): Boolean {
        return row in 0 until numRows && col in 0 until numCols
    }
}
