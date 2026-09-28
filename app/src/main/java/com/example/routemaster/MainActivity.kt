package com.example.routemaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.routemaster.data.GamePreferencesRepository
import com.example.routemaster.data.GameSettings
import com.example.routemaster.data.GameStatistics
import com.example.routemaster.ui.navigation.RouteMasterNavHost
import com.example.routemaster.ui.theme.RouteMasterTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val repository = remember { GamePreferencesRepository(applicationContext) }
            val settings by repository.settingsFlow.collectAsState(initial = GameSettings())
            val statistics by repository.statisticsFlow.collectAsState(initial = GameStatistics())

            RouteMasterTheme(appThemeSetting = settings.theme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RouteMasterNavHost(
                        statistics = statistics,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}