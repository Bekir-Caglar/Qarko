package com.bekircaglar.qarko

import platform.Foundation.NSUserDefaults
import platform.UIKit.UIApplication
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.UIWindow

actual object ThemeManager {
    private const val KEY_DARK_THEME = "dark_theme"
    private const val KEY_FIRST_LAUNCH = "first_launch"

    private val userDefaults = NSUserDefaults.standardUserDefaults

    actual fun initialize() {
        // İlk açılışta sistem temasını kontrol et
        handleFirstLaunch()
        applyTheme()
    }

    private fun handleFirstLaunch() {
        // İlk açılış kontrolü - iOS'ta objectForKey null dönerse ilk açılış demektir
        val isFirstLaunch = userDefaults.objectForKey(KEY_FIRST_LAUNCH) == null

        if (isFirstLaunch) {
            // Sistem temasını kontrol et
            val isSystemDark = isSystemInDarkTheme()

            // Sistem temasını tercihe kaydet
            userDefaults.setBool(isSystemDark, KEY_DARK_THEME)
            userDefaults.setBool(false, KEY_FIRST_LAUNCH)
            userDefaults.synchronize()
        }
    }

    private fun isSystemInDarkTheme(): Boolean {
        return try {
            // iOS 13+ için trait collection kontrol et
            val windows = UIApplication.sharedApplication.windows
            if (windows.isNotEmpty()) {
                val window = windows.first() as? UIWindow
                window?.traitCollection?.userInterfaceStyle == UIUserInterfaceStyle.UIUserInterfaceStyleDark
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    actual fun toggleTheme() {
        val currentTheme = isDarkTheme()
        val newTheme = !currentTheme

        userDefaults.setBool(newTheme, KEY_DARK_THEME)
        userDefaults.synchronize()

        applyTheme()
    }

    actual fun isDarkTheme(): Boolean {
        return userDefaults.boolForKey(KEY_DARK_THEME)
    }

    private fun applyTheme() {
        try {
            val style = if (isDarkTheme()) {
                UIUserInterfaceStyle.UIUserInterfaceStyleDark
            } else {
                UIUserInterfaceStyle.UIUserInterfaceStyleLight
            }

            // Tüm windows'ları güncelle
            val windows = UIApplication.sharedApplication.windows
            windows.forEach { window ->
                (window as? UIWindow)?.overrideUserInterfaceStyle = style
            }
        } catch (e: Exception) {
            // Hata durumunda sessizce geç
            println("ThemeManager: Error applying theme - ${e.message}")
        }
    }
}