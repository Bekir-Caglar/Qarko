package com.bekircaglar.qarko

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

actual object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_DARK_THEME = "dark_theme"
    private const val KEY_FIRST_LAUNCH = "first_launch"

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var applicationContext: Context

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
        sharedPreferences = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        handleFirstLaunch()
        applyTheme()
    }

    actual fun initialize() {
        // Android'de context gerekli olduğu için bu boş kalacak
        // MainActivity'de initialize(context) çağrılmalı
    }

    private fun handleFirstLaunch() {
        val isFirstLaunch = sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)

        if (isFirstLaunch) {
            // Sistem temasını kontrol et
            val isSystemDark = isSystemInDarkTheme()

            // Sistem temasını tercihe kaydet
            sharedPreferences.edit()
                .putBoolean(KEY_DARK_THEME, isSystemDark)
                .putBoolean(KEY_FIRST_LAUNCH, false)
                .apply()
        }
    }

    private fun isSystemInDarkTheme(): Boolean {
        return when (applicationContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }
    }

    actual fun toggleTheme() {
        val currentTheme = isDarkTheme()
        val newTheme = !currentTheme

        sharedPreferences.edit()
            .putBoolean(KEY_DARK_THEME, newTheme)
            .apply()

        applyTheme()
    }

    actual fun isDarkTheme(): Boolean {
        return if (::sharedPreferences.isInitialized) {
            sharedPreferences.getBoolean(KEY_DARK_THEME, false)
        } else {
            false
        }
    }

    private fun applyTheme() {
        val nightMode = if (isDarkTheme()) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }

        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}