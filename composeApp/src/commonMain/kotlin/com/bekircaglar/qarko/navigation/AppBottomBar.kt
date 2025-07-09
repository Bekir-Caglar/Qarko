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
import com.bekircaglar.qarko.black
import com.bekircaglar.qarko.darkPrimary
import com.bekircaglar.qarko.lightBlue
import com.bekircaglar.qarko.lightGray
import com.bekircaglar.qarko.primary
import com.bekircaglar.qarko.white
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.home
import qarko.composeapp.generated.resources.home_filled
import qarko.composeapp.generated.resources.offer
import qarko.composeapp.generated.resources.offer_filled
import qarko.composeapp.generated.resources.profile
import qarko.composeapp.generated.resources.profile_filled
import qarko.composeapp.generated.resources.search
import qarko.composeapp.generated.resources.search_filled

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
                val selected = currentRoute == item.route
                val indicatorColor by animateColorAsState(
                    if (selected && isAndroid) primary.copy(0.2f) else Color.Transparent,
                    label = "bottom_bar_indicator_color"
                )
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        val iconSize = if (selected) {
                            if (!isAndroid) 22.dp else 20.dp
                        } else {
                            if (!isAndroid) 22.dp else 20.dp
                        }

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
    val unSelectedIconPainter: Painter?,
    val selectedIconPainter: Painter? = null,
    val label: String
)

@Composable
fun defaultBottomBarItems(): List<BottomBarItem> {
    return listOf(
        BottomBarItem(
            route = Screen.TenantMenu.route,
            unSelectedIconPainter = painterResource(Res.drawable.home),
            selectedIconPainter = painterResource(Res.drawable.home_filled),
            label = "Menü"
        ),
        BottomBarItem(
            route = Screen.Search.route,
            unSelectedIconPainter = painterResource(Res.drawable.search),
            selectedIconPainter = painterResource(Res.drawable.search_filled),
            label = "Ara"
        ),
        BottomBarItem(route = "", unSelectedIconPainter = null, label = ""),
        BottomBarItem(
            route = Screen.Campaign.route,
            unSelectedIconPainter = painterResource(Res.drawable.offer),
            selectedIconPainter = painterResource(Res.drawable.offer_filled),
            label = "Kampanyalar"
        ),
        BottomBarItem(
            route = Screen.Auth.route,
            unSelectedIconPainter = painterResource(Res.drawable.profile),
            selectedIconPainter = painterResource(Res.drawable.profile_filled),
            label = "Profil"
        )
    )
}
