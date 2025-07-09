package com.bekircaglar.qarko.presentation.common.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.bekircaglar.qarko.util.QarkoTypography

// QarkoColors data class to hold all custom colors
data class QarkoColors(
    val primary: Color,
    val darkPrimary: Color,
    val white: Color,
    val black: Color,
    val gray: Color,
    val lightGray: Color,
    val lighterGray: Color,
    val darkGray: Color,
    val surfaceGray: Color,
    val orange: Color,
    val darkBlue: Color,
    val lightBlue: Color,
    val navyBlue: Color,
    val yellow: Color,
    val darkGreen: Color
)

// Light theme colors
private val LightQarkoColors = QarkoColors(
    primary = Color(0xFF51C4D3),
    darkPrimary = Color(0xFF126E82),
    white = Color(0xFFFFFFFF),
    black = Color(0xFF000000),
    gray = Color(0xFF979797),
    lightGray = Color(0xFFE0DFDF),
    lighterGray = Color(0xFFECECEC),
    darkGray = Color(0xFF4B4B4B),
    surfaceGray = Color(0xFFFAFAFA),
    orange = Color(0xFFfb7433),
    darkBlue = Color(0xFF132C33),
    lightBlue = Color(0xFFebf0f4),
    navyBlue = Color(0xFF171c2e),
    yellow = Color(0xFFFFB800),
    darkGreen = Color(0xFF2E7D32)
)

// Dark theme colors
private val DarkQarkoColors = QarkoColors(
    primary = Color(0xFF51C4D3),
    darkPrimary = Color(0xFF6DD5E8),
    white = Color(0xFF121212),
    black = Color(0xFFFFFFFF),
    gray = Color(0xFF979797),
    lightGray = Color(0xFF2C2C2C),
    lighterGray = Color(0xFF1E1E1E),
    darkGray = Color(0xFFB4B4B4),
    surfaceGray = Color(0xFF1A1A1A),
    orange = Color(0xFFfb7433),
    darkBlue = Color(0xFFE1F0F4),
    lightBlue = Color(0xFF2A3B3F),
    navyBlue = Color(0xFFE8ECF4),
    yellow = Color(0xFFFFB800),
    darkGreen = Color(0xFF4CAF50)
)

// CompositionLocal for QarkoColors
val LocalQarkoColors = staticCompositionLocalOf { LightQarkoColors }

// Material Theme color schemes
private val LightColorScheme = lightColorScheme(
    primary = LightQarkoColors.primary,
    onPrimary = LightQarkoColors.white,
    secondary = Color(0xFF03DAC6),
    onSecondary = LightQarkoColors.black,
    background = LightQarkoColors.surfaceGray,
    onBackground = LightQarkoColors.black,
    surface = LightQarkoColors.white,
    onSurface = LightQarkoColors.black,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkQarkoColors.primary,
    onPrimary = DarkQarkoColors.black,
    secondary = Color(0xFF03DAC6),
    onSecondary = DarkQarkoColors.white,
    background = DarkQarkoColors.surfaceGray,
    onBackground = DarkQarkoColors.black,
    surface = DarkQarkoColors.white,
    onSurface = DarkQarkoColors.black,
)

@Composable
fun QarkoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkQarkoColors else LightQarkoColors
    val materialColors = if (darkTheme) DarkColorScheme else LightColorScheme
    
    CompositionLocalProvider(LocalQarkoColors provides colors) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = QarkoTypography,
            content = content
        )
    }
}

// Object to access QarkoColors from any Composable
object QarkoTheme {
    val colors: QarkoColors
        @Composable
        get() = LocalQarkoColors.current
}