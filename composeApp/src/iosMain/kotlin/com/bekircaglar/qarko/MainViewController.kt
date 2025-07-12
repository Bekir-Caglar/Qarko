package com.bekircaglar.qarko

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {

    ThemeManager.initialize()
    App()
}