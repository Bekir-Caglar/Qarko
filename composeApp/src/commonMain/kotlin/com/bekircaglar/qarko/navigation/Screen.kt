package com.bekircaglar.qarko.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector


object NavRoutes {
    const val FOOD_DETAIL = "food_detail"
    const val TENANT_MENU = "tenant_menu"
    const val CART = "cart"
    const val WELCOME = "welcome"
    const val QR_SCAN = "qr_scan"
}

sealed class Screen(val route: String, val icon: ImageVector? = null, val title: String = "") {
    object FoodDetail : Screen(NavRoutes.FOOD_DETAIL)
    object TenantMenu : Screen(NavRoutes.TENANT_MENU, Icons.Default.Person, "Menu")
    object Cart : Screen(NavRoutes.CART, Icons.Default.ShoppingCart, "Cart")
    object Welcome : Screen(NavRoutes.WELCOME, title = "Welcome")
    object QRScan : Screen(NavRoutes.QR_SCAN, title = "QR Scan")
}