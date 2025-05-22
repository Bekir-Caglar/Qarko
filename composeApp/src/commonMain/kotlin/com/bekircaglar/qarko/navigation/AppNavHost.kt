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

/**
 * Main navigation component that defines the app's navigation graph
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = NavRoutes.FEED,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(NavRoutes.FEED) {
             FeedScreen(navController)
        }

        composable(NavRoutes.TENANT_MENU) {
             TenantMenuScreen(navController)
        }

        composable(
            route = NavRoutes.FOOD_DETAIL,
        ) { backStackEntry ->
            FoodDetailScreen(navController)
        }

        composable(NavRoutes.CART) {
            CartScreen(navController)
        }


    }
}