package com.navloka.arrowrush.ui.theme

import androidx.compose.ui.graphics.Color

enum class AppThemeMode {
    CYBER,
    ZEN,
    SUNSET
}

data class GameThemeColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val boardBackground: Color,
    val gridLine: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val arrowColors: List<Color>,
    val arrowBorder: Color,
    val blockerHighlight: Color,
    val hintGlow: Color,
    val starActive: Color,
    val starInactive: Color
)

val CyberColors = GameThemeColors(
    background = Color(0xFF090D16),
    surface = Color(0xFF131C2E),
    surfaceVariant = Color(0xFF1E293B),
    boardBackground = Color(0xFF0D1527),
    gridLine = Color(0x1A60A5FA),
    textPrimary = Color(0xFFF1F5F9),
    textSecondary = Color(0xFF94A3B8),
    accent = Color(0xFF38BDF8),
    arrowColors = listOf(
        Color(0xFF38BDF8), // Cyan
        Color(0xFF818CF8), // Indigo
        Color(0xFFFB7185), // Rose
        Color(0xFF34D399)  // Emerald
    ),
    arrowBorder = Color(0x40FFFFFF),
    blockerHighlight = Color(0xFFEF4444),
    hintGlow = Color(0xFFFBBF24),
    starActive = Color(0xFFFBBF24),
    starInactive = Color(0xFF334155)
)

val ZenColors = GameThemeColors(
    background = Color(0xFFF7F4EB),
    surface = Color(0xFFEBE6D8),
    surfaceVariant = Color(0xFFDDD7C6),
    boardBackground = Color(0xFFE4DEC9),
    gridLine = Color(0x26000000),
    textPrimary = Color(0xFF1F2421),
    textSecondary = Color(0xFF5A635B),
    accent = Color(0xFF2E6F54),
    arrowColors = listOf(
        Color(0xFF2E6F54), // Forest green
        Color(0xFFC45A3C), // Terracotta
        Color(0xFF2C5D77), // Slate Blue
        Color(0xFF9E6B28)  // Ochre
    ),
    arrowBorder = Color(0x33000000),
    blockerHighlight = Color(0xFFDC2626),
    hintGlow = Color(0xFFD97706),
    starActive = Color(0xFFD97706),
    starInactive = Color(0xFFC7C0AF)
)

val SunsetColors = GameThemeColors(
    background = Color(0xFF150D24),
    surface = Color(0xFF231738),
    surfaceVariant = Color(0xFF32234D),
    boardBackground = Color(0xFF1C1130),
    gridLine = Color(0x1AF43F5E),
    textPrimary = Color(0xFFFFF1F2),
    textSecondary = Color(0xFFFDA4AF),
    accent = Color(0xFFF43F5E),
    arrowColors = listOf(
        Color(0xFFF43F5E), // Coral Red
        Color(0xFFFB923C), // Amber Orange
        Color(0xFFA855F7), // Purple
        Color(0xFF06B6D4)  // Teal
    ),
    arrowBorder = Color(0x40FFFFFF),
    blockerHighlight = Color(0xFFE11D48),
    hintGlow = Color(0xFFFDE047),
    starActive = Color(0xFFFACC15),
    starInactive = Color(0xFF4A3860)
)
