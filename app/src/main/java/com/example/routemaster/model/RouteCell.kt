package com.example.routemaster.model

sealed class RouteCell {
    data object Empty : RouteCell()
    data object Blocked : RouteCell()
    data class Tile(val tile: RouteTile) : RouteCell()
}
