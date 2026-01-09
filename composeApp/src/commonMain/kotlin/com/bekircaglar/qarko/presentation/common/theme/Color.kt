package com.bekircaglar.qarko.presentation.common.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Theme-aware color accessors
val primary: Color 
    @Composable get() = QarkoTheme.colors.primary

val white: Color 
    @Composable get() = QarkoTheme.colors.white

val black: Color 
    @Composable get() = QarkoTheme.colors.black

val surfaceGray: Color 
    @Composable get() = QarkoTheme.colors.surfaceGray

val darkPrimary: Color 
    @Composable get() = QarkoTheme.colors.darkPrimary

val gray: Color 
    @Composable get() = QarkoTheme.colors.gray

val orange: Color 
    @Composable get() = QarkoTheme.colors.orange

val darkBlue: Color 
    @Composable get() = QarkoTheme.colors.darkBlue

val lightBlue: Color 
    @Composable get() = QarkoTheme.colors.lightBlue

val navyBlue: Color 
    @Composable get() = QarkoTheme.colors.navyBlue

val darkGray: Color 
    @Composable get() = QarkoTheme.colors.darkGray

val yellow: Color 
    @Composable get() = QarkoTheme.colors.yellow

val lightGray: Color 
    @Composable get() = QarkoTheme.colors.lightGray

val lighterGray: Color 
    @Composable get() = QarkoTheme.colors.lighterGray

val darkGreen: Color 
    @Composable get() = QarkoTheme.colors.darkGreen

// Legacy static colors for non-Composable contexts (if needed)
object QarkoLegacyColors {
    val white = Color(0xFFFFFFFF)
    val black = Color(0xFF000000)
    val primary = Color(0xFF51C4D3)
    val surfaceGray = Color(0xFFf5f5f5)
    val darkPrimary = Color(0xFF126E82)
    val gray = Color(0xFF979797)
    val orange = Color(0xFFfb7433)
    val darkBlue = Color(0xFF132C33)
    val lightBlue = Color(0xFFebf0f4)
    val navyBlue = Color(0xFF171c2e)
    val darkGray = Color(0xFF4B4B4B)
    val yellow = Color(0xFFFFB800)
    val lightGray = Color(0xFFE0DFDF)
    val lighterGray = Color(0xFFECECEC)
    val darkGreen = Color(0xFF2E7D32)
}