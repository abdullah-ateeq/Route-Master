package com.example.routemaster.data

enum class AppThemeSetting {
    SYSTEM,
    DARK,
    LIGHT
}

enum class BoardThemeSetting {
    CLASSIC,  // Ivory & Bronze Slate
    MIDNIGHT  // Obsidian & Cyber Teal
}

data class GameSettings(
    val theme: AppThemeSetting = AppThemeSetting.DARK,
    val boardTheme: BoardThemeSetting = BoardThemeSetting.MIDNIGHT,
    val soundEffects: Boolean = true,
    val hapticFeedback: Boolean = true,
    val highlightLongestRoute: Boolean = true,
    val showTimer: Boolean = true
)

data class GameStatistics(
    val gamesPlayed: Int = 0,
    val bestRouteLength: Int = 0,
    val bestBoardCoveragePercent: Int = 0,
    val bestMoves: Int = 0,
    val bestTimeSeconds: Long = 0L
)
