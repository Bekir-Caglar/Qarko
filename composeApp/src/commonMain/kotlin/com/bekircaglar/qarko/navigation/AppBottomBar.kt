package com.bekircaglar.qarko.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bekircaglar.getPlatformName
import com.bekircaglar.qarko.data.manager.UserManager
import com.bekircaglar.qarko.presentation.common.theme.black
import com.bekircaglar.qarko.presentation.common.theme.darkPrimary
import com.bekircaglar.qarko.presentation.common.theme.lightBlue
import com.bekircaglar.qarko.presentation.common.theme.lightGray
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.presentation.common.theme.white
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.ic_gift
import qarko.composeapp.generated.resources.ic_gift_filled
import qarko.composeapp.generated.resources.ic_home
import qarko.composeapp.generated.resources.ic_home_filled
import qarko.composeapp.generated.resources.ic_profile
import qarko.composeapp.generated.resources.ic_profile_filled
import qarko.composeapp.generated.resources.ic_waiter
import qarko.composeapp.generated.resources.ic_waiter_filled

@Composable
fun AppBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier,
    currentRoute: String,
    items: List<BottomBarItem> = defaultBottomBarItems()
) {

    val isAndroid by remember { mutableStateOf(getPlatformName() == "ANDROID") }

    Column {
        HorizontalDivider(
            color = lightGray,
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, shape = RoundedCornerShape(0.dp))
        )

        NavigationBar(
            modifier = modifier.background(white).padding(horizontal = 16.dp).height(72.dp),
            containerColor = white,
        ) {
            items.forEach { item ->
                // Type-safe navigation routes are the qualified names of the classes
                val selected = currentRoute.contains(item.route)
                val indicatorColor by animateColorAsState(
                    if (selected && isAndroid) primary.copy(0.2f) else Color.Transparent,
                    label = "bottom_bar_indicator_color"
                )
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected && item.destination != null) {
                            navController.navigate(item.destination) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        val iconSize = 22.dp

                        Crossfade(
                            targetState = selected,
                            label = "bottom_bar_icon_crossfade"
                        ) { isSelected ->
                            val painter =
                                if (isSelected) item.selectedIconPainter else item.unSelectedIconPainter
                            painter?.let {
                                Icon(
                                    painter = it,
                                    contentDescription = item.label,
                                    tint = if (isSelected) primary else black,
                                    modifier = Modifier.size(iconSize)
                                )
                            }
                        }
                    },
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = indicatorColor,
                        selectedIconColor = primary,
                        unselectedIconColor = black,
                        selectedTextColor = darkPrimary,
                        unselectedTextColor = lightBlue
                    )
                )
            }
        }
    }
}

data class BottomBarItem(
    val route: String,
    val destination: Any? = null,
    val unSelectedIconPainter: Painter?,
    val selectedIconPainter: Painter? = null,
    val label: String
)

@Composable
fun defaultBottomBarItems(): List<BottomBarItem> {
    val isLoggedIn = UserManager.isLoggedIn

    return listOf(
        BottomBarItem(
            route = TenantMenu::class.qualifiedName ?: Screens.TENANT_MENU,
            destination = TenantMenu,
            unSelectedIconPainter = painterResource(Res.drawable.ic_home),
            selectedIconPainter = painterResource(Res.drawable.ic_home_filled),
            label = "Menü"
        ),
        BottomBarItem(
            route = Orders::class.qualifiedName ?: Screens.ORDERS,
            destination = Orders,
            unSelectedIconPainter = painterResource(Res.drawable.ic_waiter),
            selectedIconPainter = painterResource(Res.drawable.ic_waiter_filled),
            label = "Siparişler"
        ),
        BottomBarItem(route = "empty", destination = null, unSelectedIconPainter = null, label = ""),
        BottomBarItem(
            route = Campaign::class.qualifiedName ?: Screens.CAMPAIGN,
            destination = Campaign,
            unSelectedIconPainter = painterResource(Res.drawable.ic_gift),
            selectedIconPainter = painterResource(Res.drawable.ic_gift_filled),
            label = "Kampanyalar"
        ),
        BottomBarItem(
            route = if (isLoggedIn) Profile::class.qualifiedName ?: Screens.PROFILE else Auth::class.qualifiedName ?: Screens.AUTH,
            destination = if (isLoggedIn) Profile else Auth,
            unSelectedIconPainter = painterResource(Res.drawable.ic_profile),
            selectedIconPainter = painterResource(Res.drawable.ic_profile_filled),
            label = "Profil"
        )
    )
}
