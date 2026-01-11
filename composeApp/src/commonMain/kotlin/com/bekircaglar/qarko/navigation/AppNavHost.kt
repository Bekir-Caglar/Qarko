package com.bekircaglar.qarko.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.bekircaglar.qarko.presentation.auth.AuthScreen
import com.bekircaglar.qarko.presentation.auth.login.LoginScreen
import com.bekircaglar.qarko.presentation.auth.register.RegisterScreen
import com.bekircaglar.qarko.presentation.campaign.CampaignScreen
import com.bekircaglar.qarko.presentation.cart.CartScreen
import com.bekircaglar.qarko.presentation.orders.OrdersScreen
import com.bekircaglar.qarko.presentation.food_detail.FoodDetailScreen
import com.bekircaglar.qarko.presentation.profile.ProfileScreen
import com.bekircaglar.qarko.presentation.tenant.TenantMenuScreen
import com.bekircaglar.qarko.presentation.welcome.QRScanScreen
import com.bekircaglar.qarko.presentation.welcome.WelcomeScreen

/**
 * Main navigation component that defines the app's navigation graph
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Welcome,
        modifier = modifier
    ) {
        composable<Welcome> {
            WelcomeScreen(navController)
        }

        composable<QRScan> {
            QRScanScreen(navController)
        }

        composable<TenantMenu> {
            TenantMenuScreen(navController)
        }

        composable<FoodDetail> { backStackEntry ->
            val foodDetail: FoodDetail = backStackEntry.toRoute()
            val foodItem = foodDetail.toFoodItem()
            FoodDetailScreen(navController = navController, foodItem = foodItem)
        }

        composable<Cart> {
            CartScreen(navController)
        }

        composable<Orders> {
            OrdersScreen(navController)
        }

        composable<Campaign> {
            CampaignScreen(navController)
        }

        composable<Profile> {
            ProfileScreen(navController)
        }

        composable<Auth> {
            AuthScreen(navController)
        }

        composable<Login> {
            LoginScreen(navController)
        }

        composable<Register> {
            RegisterScreen(navController)
        }

        composable<Otp> {
            // OtpScreen(navController)
        }

    }
}
