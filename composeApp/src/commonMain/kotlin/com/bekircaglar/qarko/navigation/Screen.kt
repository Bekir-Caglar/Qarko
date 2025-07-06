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
    const val SEARCH = "search"
    const val CAMPAIGN = "campaign"
    const val PROFILE = "profile"
    const val AUTH = "auth"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val OTP = "otp"
}

sealed class Screen(
    val route: String,
    val icon: ImageVector? = null,
    val title: String = "",
    badgeCount: Int = 0
) {
    object FoodDetail : Screen(NavRoutes.FOOD_DETAIL)
    object TenantMenu : Screen(NavRoutes.TENANT_MENU, Icons.Default.Person, "Menu")
    object Cart : Screen(NavRoutes.CART, Icons.Default.ShoppingCart)
    object Welcome : Screen(NavRoutes.WELCOME, title = "Welcome")
    object QRScan : Screen(NavRoutes.QR_SCAN, title = "QR Scan")
    object Search : Screen(NavRoutes.SEARCH, title = "Search")
    object Campaign : Screen(
        NavRoutes.CAMPAIGN,
        title = "Campaigns",
    )

    object Profile : Screen(
        NavRoutes.PROFILE,
        title = "Profile"
    )

    object Auth : Screen(
        NavRoutes.AUTH,
        title = "Authentication"
    )

    object Login : Screen(
        NavRoutes.LOGIN,
        title = "Login"
    )

    object Register : Screen(
        NavRoutes.REGISTER,
        title = "Register"
    )

    object Otp : Screen(
        NavRoutes.OTP,
        title = "OTP Verification"
    )
}