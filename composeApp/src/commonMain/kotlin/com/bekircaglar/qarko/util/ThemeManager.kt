package com.bekircaglar.qarko

expect object ThemeManager {
    fun initialize()
    fun toggleTheme()
    fun isDarkTheme(): Boolean
}