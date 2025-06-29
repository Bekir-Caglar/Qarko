package com.bekircaglar.qarko.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bekircaglar.qarko.presentation.cart.CartScreen
import com.bekircaglar.qarko.presentation.feed.FeedScreen
import com.bekircaglar.qarko.presentation.food_detail.FoodDetailScreen
import com.bekircaglar.qarko.presentation.tenant.TenantMenuScreen
import com.bekircaglar.qarko.presentation.welcome.QRScanScreen
import com.bekircaglar.qarko.presentation.welcome.WelcomeScreen

/**
 * Main navigation component that defines the app's navigation graph
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = NavRoutes.WELCOME,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NavRoutes.WELCOME) {
            WelcomeScreen(navController)
        }

        composable(NavRoutes.QR_SCAN) {
            QRScanScreen(navController)
        }

        composable(NavRoutes.FEED) {
            FeedScreen(navController)
        }

        composable(NavRoutes.TENANT_MENU) {
            TenantMenuScreen(navController)
        }

        composable(NavRoutes.FOOD_DETAIL) {
            FoodDetailScreen(navController)
        }

        composable(NavRoutes.CART) {
            CartScreen(navController)
        }
    }
}
