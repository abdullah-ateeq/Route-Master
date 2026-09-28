package com.example.routemaster.engine

import com.example.routemaster.model.RouteTile
import com.example.routemaster.model.TileKind
import kotlin.random.Random

object TileBag {
    const val TRAY_SIZE = 3

    // Deterministic distribution: 18 tiles total
    const val STRAIGHT_COUNT = 6
    const val CORNER_COUNT = 6
    const val T_JUNCTION_COUNT = 4
    const val CROSS_COUNT = 2
    const val TOTAL_TILE_COUNT = STRAIGHT_COUNT + CORNER_COUNT + T_JUNCTION_COUNT + CROSS_COUNT

    fun generateDeck(random: Random = Random.Default): List<RouteTile> {
        val tiles = mutableListOf<RouteTile>()
        var idCounter = 1

        repeat(STRAIGHT_COUNT) {
            tiles.add(RouteTile(id = idCounter++, kind = TileKind.STRAIGHT, rotation = 0))
        }
        repeat(CORNER_COUNT) {
            tiles.add(RouteTile(id = idCounter++, kind = TileKind.CORNER, rotation = 0))
        }
        repeat(T_JUNCTION_COUNT) {
            tiles.add(RouteTile(id = idCounter++, kind = TileKind.T_JUNCTION, rotation = 0))
        }
        repeat(CROSS_COUNT) {
            tiles.add(RouteTile(id = idCounter++, kind = TileKind.CROSS, rotation = 0))
        }

        return tiles.shuffled(random)
    }

    /**
     * Splits generated deck into initial tray of [TRAY_SIZE] tiles and the remaining bag.
     */
    fun dealInitial(deck: List<RouteTile>): Pair<List<RouteTile>, List<RouteTile>> {
        val tray = deck.take(TRAY_SIZE)
        val remainingBag = deck.drop(TRAY_SIZE)
        return Pair(tray, remainingBag)
    }
}
