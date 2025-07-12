package com.bekircaglar.qarko

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ThemeManager.initialize(this)
            val systemUiController = rememberSystemUiController()
            val isDarkTheme = ThemeManager.isDarkTheme()

            DisposableEffect(systemUiController) {
                systemUiController.setSystemBarsColor(
                    color = if (isDarkTheme) Color(0xFF121212) else Color.White,
                    darkIcons = !isDarkTheme
                )

                onDispose {}
            }
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}