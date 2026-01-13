package com.bekircaglar.qarko

import androidx.compose.ui.window.ComposeUIViewController

// iOS tarafında Koin'in sadece bir kez başlatıldığından emin olmak için bir flag
private var isKoinInitialized = false

fun MainViewController() = ComposeUIViewController {
    if (!isKoinInitialized) {
        initKoin()
        isKoinInitialized = true
    }
    
    ThemeManager.initialize()
    App()
}
