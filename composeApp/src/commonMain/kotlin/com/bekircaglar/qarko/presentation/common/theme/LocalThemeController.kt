package com.bekircaglar.qarko.presentation.common.theme

import androidx.compose.runtime.*
import com.bekircaglar.qarko.ThemeManager
import com.bekircaglar.qarko.util.ThemeController

val LocalThemeController = compositionLocalOf<ThemeController> {
    error("ThemeController not provided")
}

@Composable
fun QarkoThemeProvider(
    content: @Composable () -> Unit
) {
    var isDarkTheme by remember { mutableStateOf(ThemeManager.isDarkTheme()) }
    
    val themeController = remember {
        object : ThemeController {
            override val isDarkTheme: Boolean get() = isDarkTheme
            
            override fun toggleTheme() {
                ThemeManager.toggleTheme()
                isDarkTheme = ThemeManager.isDarkTheme()
            }
        }
    }
    
    CompositionLocalProvider(
        LocalThemeController provides themeController
    ) {
        QarkoTheme(darkTheme = isDarkTheme) {
            content()
        }
    }
}