package com.example.routemaster.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.routemaster.data.AppThemeSetting
import com.example.routemaster.data.BoardThemeSetting
import com.example.routemaster.data.GamePreferencesRepository
import com.example.routemaster.data.GameSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GamePreferencesRepository(application)

    val settings: StateFlow<GameSettings> = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GameSettings()
    )

    fun setAppTheme(theme: AppThemeSetting) {
        viewModelScope.launch { repository.setAppTheme(theme) }
    }

    fun setBoardTheme(boardTheme: BoardThemeSetting) {
        viewModelScope.launch { repository.setBoardTheme(boardTheme) }
    }

    fun setSoundEffects(enabled: Boolean) {
        viewModelScope.launch { repository.setSoundEffects(enabled) }
    }

    fun setHapticFeedback(enabled: Boolean) {
        viewModelScope.launch { repository.setHapticFeedback(enabled) }
    }

    fun setHighlightLongestRoute(enabled: Boolean) {
        viewModelScope.launch { repository.setHighlightLongestRoute(enabled) }
    }

    fun setShowTimer(enabled: Boolean) {
        viewModelScope.launch { repository.setShowTimer(enabled) }
    }

    fun resetStatistics() {
        viewModelScope.launch { repository.resetStatistics() }
    }
}
