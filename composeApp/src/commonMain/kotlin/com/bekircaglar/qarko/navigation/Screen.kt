package com.bekircaglar.qarko.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector


object NavRoutes {
    const val FEED = "feed"
    const val FOOD_DETAIL = "food_detail}"
    const val TENANT_MENU = "tenant_menu"
}

sealed class Screen(val route: String, val icon: ImageVector? = null, val title: String = "") {
    object Home : Screen(NavRoutes.FEED, Icons.Default.Home, "Feed")
    object FoodDetail : Screen(NavRoutes.FOOD_DETAIL)
    object TenantMenu : Screen(NavRoutes.TENANT_MENU, Icons.Default.Person, "Menu")

    // Bottom navigation screens
    companion object {
        val bottomNavScreens = listOf(Home, TenantMenu, FoodDetail )
    }
}