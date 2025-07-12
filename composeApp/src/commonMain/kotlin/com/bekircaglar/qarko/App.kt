package com.bekircaglar.qarko

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.bekircaglar.qarko.navigation.AppNavHost
import com.bekircaglar.qarko.presentation.common.theme.QarkoTheme
import com.bekircaglar.qarko.presentation.common.theme.QarkoThemeProvider
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    QarkoTheme {
        val navHost = rememberNavController()
        QarkoThemeProvider {
            AppNavHost(
                navController = navHost,
            )
        }
    }
}