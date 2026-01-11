package com.bekircaglar.qarko.presentation.cart

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekircaglar.qarko.presentation.common.theme.black
import com.bekircaglar.qarko.presentation.common.theme.darkBlue
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.presentation.common.theme.lightGray
import com.bekircaglar.qarko.data.manager.CartManager
import com.bekircaglar.qarko.presentation.cart.component.CardDetails
import com.bekircaglar.qarko.presentation.cart.component.CardPaymentTab
import com.bekircaglar.qarko.presentation.cart.component.CashPaymentTab
import com.bekircaglar.qarko.presentation.cart.component.OrderButtonComponent
import com.bekircaglar.qarko.presentation.cart.component.PaymentMethodRow
import com.bekircaglar.qarko.presentation.cart.component.PaymentMethodSheet
import com.bekircaglar.qarko.presentation.common.components.BackButton
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.util.toPriceString
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.presentation.common.theme.white
import compose.icons.FeatherIcons
import compose.icons.feathericons.CreditCard
import compose.icons.feathericons.DollarSign
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.ic_trash

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController) {
    val cartItems = CartManager.cartItems
    val scope = rememberCoroutineScope()

    var savedCard by remember { mutableStateOf<CardDetails?>(null) }
    var showPaymentSheet by remember { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState()

    // Pager state for swipeable tabs
    val pagerState = rememberPagerState(initialPage = 0) { 2 }

    // Calculate total reactively
    val total by remember {
        derivedStateOf {
            cartItems.sumOf { it.price * it.quantity }
        }
    }

    val tabs = listOf(
        TabItem("Kart ile öde", FeatherIcons.CreditCard),
        TabItem("Kasada öde", FeatherIcons.DollarSign)
    )

    Scaffold(
        containerColor = white,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = white,
                ),
                title = {
                    QText(
                        text = "Sepetim",
                        fontSize = 20.sp,
                        color = black,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        IconButton(
                            onClick = { CartManager.clearCart() }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_trash),
                                contentDescription = "Sepeti Temizle",
                                tint = black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    BackButton { navController.popBackStack() }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (cartItems.isEmpty()) {
                // Empty cart state
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    QText(text = "🛒", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    QText(
                        text = "Sepetiniz boş",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    QText(
                        text = "Lezzetli ürünlerimizi keşfedin!",
                        fontSize = 14.sp,
                        color = gray
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Native TabRow
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = white,
                        contentColor = primary,
                        indicator = { tabPositions ->
                            Box(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                                    .height(3.dp)
                                    .padding(horizontal = 48.dp)
                                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                    .background(primary)
                            )
                        },
                        divider = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(lightGray.copy(alpha = 0.5f))
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, tab ->
                            val isSelected = pagerState.currentPage == index
                            val textColor by animateColorAsState(
                                targetValue = if (isSelected) primary else gray,
                                animationSpec = tween(300),
                                label = "tabTextColor"
                            )

                            Tab(
                                selected = isSelected,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = null,
                                        tint = textColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.size(8.dp))
                                    QText(
                                        text = tab.title,
                                        color = textColor,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    // HorizontalPager for swipeable content
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) { page ->
                        when (page) {
                            0 -> CardPaymentTab(cartItems = cartItems)
                            1 -> CashPaymentTab(cartItems = cartItems)
                        }
                    }

                    // Bottom payment section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp)
                            .background(white)
                            .padding(16.dp)
                    ) {
                        OrderButtonComponent(
                            buttonText = if (pagerState.currentPage == 1) "Sipariş Ver" else "Ödemeye Geç",
                            onButtonClick = { },
                            topContent = {
                                if (pagerState.currentPage == 0) {
                                    PaymentMethodRow(total)
                                    Spacer(modifier = Modifier.size(16.dp))
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        QText(
                                            text = "Toplam",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = total.toPriceString(),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = darkBlue
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showPaymentSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaymentSheet = false },
            sheetState = modalBottomSheetState
        ) {
            PaymentMethodSheet(
                onDismiss = { showPaymentSheet = false },
                onSave = { cardDetails ->
                    savedCard = cardDetails
                    showPaymentSheet = false
                }
            )
        }
    }
}

private data class TabItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
