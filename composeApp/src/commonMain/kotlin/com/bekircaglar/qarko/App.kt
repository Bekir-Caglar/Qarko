package com.bekircaglar.qarko

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.bekircaglar.qarko.navigation.AppNavHost
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navHost = rememberNavController()
        AppNavHost(
            navController = navHost,
        )
    }
}