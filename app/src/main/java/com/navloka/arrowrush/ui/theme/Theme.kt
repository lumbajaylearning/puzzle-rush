package com.navloka.arrowrush.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalGameColors = staticCompositionLocalOf { ZenColors }

@Composable
fun ArrowRushTheme(
    themeMode: AppThemeMode = AppThemeMode.ZEN,
    content: @Composable () -> Unit
) {
    val gameColors = when (themeMode) {
        AppThemeMode.CYBER -> CyberColors
        AppThemeMode.ZEN -> ZenColors
        AppThemeMode.SUNSET -> SunsetColors
    }

    val colorScheme = if (themeMode == AppThemeMode.ZEN) {
        lightColorScheme(
            primary = gameColors.accent,
            background = gameColors.background,
            surface = gameColors.surface,
            onBackground = gameColors.textPrimary,
            onSurface = gameColors.textPrimary
        )
    } else {
        darkColorScheme(
            primary = gameColors.accent,
            background = gameColors.background,
            surface = gameColors.surface,
            onBackground = gameColors.textPrimary,
            onSurface = gameColors.textPrimary
        )
    }

    CompositionLocalProvider(LocalGameColors provides gameColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
