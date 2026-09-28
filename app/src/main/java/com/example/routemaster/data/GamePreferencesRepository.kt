package com.example.routemaster.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "routemaster_prefs")

class GamePreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val APP_THEME = stringPreferencesKey("app_theme")
        val BOARD_THEME = stringPreferencesKey("board_theme")
        val SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val HIGHLIGHT_LONGEST_ROUTE = booleanPreferencesKey("highlight_longest_route")
        val SHOW_TIMER = booleanPreferencesKey("show_timer")

        val GAMES_PLAYED = intPreferencesKey("games_played")
        val BEST_ROUTE_LENGTH = intPreferencesKey("best_route_length")
        val BEST_BOARD_COVERAGE = intPreferencesKey("best_board_coverage")
        val BEST_MOVES = intPreferencesKey("best_moves")
        val BEST_TIME_SECONDS = longPreferencesKey("best_time_seconds")
    }

    val settingsFlow: Flow<GameSettings> = context.dataStore.data.map { prefs ->
        val themeStr = prefs[PreferencesKeys.APP_THEME] ?: AppThemeSetting.DARK.name
        val boardThemeStr = prefs[PreferencesKeys.BOARD_THEME] ?: BoardThemeSetting.MIDNIGHT.name

        GameSettings(
            theme = runCatching { AppThemeSetting.valueOf(themeStr) }.getOrDefault(AppThemeSetting.DARK),
            boardTheme = runCatching { BoardThemeSetting.valueOf(boardThemeStr) }.getOrDefault(BoardThemeSetting.MIDNIGHT),
            soundEffects = prefs[PreferencesKeys.SOUND_EFFECTS] ?: true,
            hapticFeedback = prefs[PreferencesKeys.HAPTIC_FEEDBACK] ?: true,
            highlightLongestRoute = prefs[PreferencesKeys.HIGHLIGHT_LONGEST_ROUTE] ?: true,
            showTimer = prefs[PreferencesKeys.SHOW_TIMER] ?: true
        )
    }

    val statisticsFlow: Flow<GameStatistics> = context.dataStore.data.map { prefs ->
        GameStatistics(
            gamesPlayed = prefs[PreferencesKeys.GAMES_PLAYED] ?: 0,
            bestRouteLength = prefs[PreferencesKeys.BEST_ROUTE_LENGTH] ?: 0,
            bestBoardCoveragePercent = prefs[PreferencesKeys.BEST_BOARD_COVERAGE] ?: 0,
            bestMoves = prefs[PreferencesKeys.BEST_MOVES] ?: 0,
            bestTimeSeconds = prefs[PreferencesKeys.BEST_TIME_SECONDS] ?: 0L
        )
    }

    suspend fun setAppTheme(theme: AppThemeSetting) {
        context.dataStore.edit { it[PreferencesKeys.APP_THEME] = theme.name }
    }

    suspend fun setBoardTheme(boardTheme: BoardThemeSetting) {
        context.dataStore.edit { it[PreferencesKeys.BOARD_THEME] = boardTheme.name }
    }

    suspend fun setSoundEffects(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SOUND_EFFECTS] = enabled }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.HAPTIC_FEEDBACK] = enabled }
    }

    suspend fun setHighlightLongestRoute(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.HIGHLIGHT_LONGEST_ROUTE] = enabled }
    }

    suspend fun setShowTimer(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_TIMER] = enabled }
    }

    suspend fun recordGameResult(
        routeLength: Int,
        coveragePercent: Int,
        moves: Int,
        durationSeconds: Long
    ) {
        context.dataStore.edit { prefs ->
            val games = (prefs[PreferencesKeys.GAMES_PLAYED] ?: 0) + 1
            val currentBestRoute = prefs[PreferencesKeys.BEST_ROUTE_LENGTH] ?: 0
            val currentBestCoverage = prefs[PreferencesKeys.BEST_BOARD_COVERAGE] ?: 0
            val currentBestMoves = prefs[PreferencesKeys.BEST_MOVES] ?: 0
            val currentBestTime = prefs[PreferencesKeys.BEST_TIME_SECONDS] ?: 0L

            prefs[PreferencesKeys.GAMES_PLAYED] = games
            prefs[PreferencesKeys.BEST_ROUTE_LENGTH] = maxOf(currentBestRoute, routeLength)
            prefs[PreferencesKeys.BEST_BOARD_COVERAGE] = maxOf(currentBestCoverage, coveragePercent)

            if (currentBestMoves == 0 || (moves in 1 until currentBestMoves)) {
                prefs[PreferencesKeys.BEST_MOVES] = moves
            }

            if (durationSeconds > 0 && (currentBestTime == 0L || durationSeconds < currentBestTime)) {
                prefs[PreferencesKeys.BEST_TIME_SECONDS] = durationSeconds
            }
        }
    }

    suspend fun resetStatistics() {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.GAMES_PLAYED] = 0
            prefs[PreferencesKeys.BEST_ROUTE_LENGTH] = 0
            prefs[PreferencesKeys.BEST_BOARD_COVERAGE] = 0
            prefs[PreferencesKeys.BEST_MOVES] = 0
            prefs[PreferencesKeys.BEST_TIME_SECONDS] = 0L
        }
    }
}
