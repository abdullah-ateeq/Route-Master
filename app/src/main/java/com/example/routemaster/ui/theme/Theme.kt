package com.example.routemaster.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.routemaster.data.AppThemeSetting

private val DarkColorScheme = darkColorScheme(
    primary = AccentTeal,
    onPrimary = Graphite950,
    primaryContainer = Graphite700,
    onPrimaryContainer = TrackHighlightTeal,
    secondary = TrackBronze,
    onSecondary = Graphite950,
    secondaryContainer = Graphite800,
    onSecondaryContainer = TextWhitePrimary,
    background = Graphite950,
    onBackground = TextWhitePrimary,
    surface = Graphite900,
    onSurface = TextWhitePrimary,
    surfaceVariant = Graphite800,
    onSurfaceVariant = TextGraySecondary,
    outline = Graphite600
)

private val LightColorScheme = lightColorScheme(
    primary = AccentTeal,
    onPrimary = Color.White,
    primaryContainer = SurfaceLightCard,
    onPrimaryContainer = Graphite900,
    secondary = TrackBronze,
    onSecondary = Color.White,
    secondaryContainer = SurfaceLight,
    onSecondaryContainer = Graphite900,
    background = SurfaceLight,
    onBackground = Graphite950,
    surface = Color.White,
    onSurface = Graphite950,
    surfaceVariant = SurfaceLightCard,
    onSurfaceVariant = Graphite700,
    outline = SurfaceLightBorder
)

@Composable
fun RouteMasterTheme(
    appThemeSetting: AppThemeSetting = AppThemeSetting.DARK,
    content: @Composable () -> Unit
) {
    val isDark = when (appThemeSetting) {
        AppThemeSetting.SYSTEM -> isSystemInDarkTheme()
        AppThemeSetting.DARK -> true
        AppThemeSetting.LIGHT -> false
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}