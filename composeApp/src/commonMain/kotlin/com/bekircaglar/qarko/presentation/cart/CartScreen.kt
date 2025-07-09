package com.bekircaglar.qarko.presentation.cart

import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekircaglar.qarko.black
import com.bekircaglar.qarko.darkBlue
import com.bekircaglar.qarko.data.model.CartItemData
import com.bekircaglar.qarko.presentation.cart.component.CardDetails
import com.bekircaglar.qarko.presentation.cart.component.CardPaymentTab
import com.bekircaglar.qarko.presentation.cart.component.CashPaymentTab
import com.bekircaglar.qarko.presentation.cart.component.GenericTabRow
import com.bekircaglar.qarko.presentation.cart.component.OrderButtonComponent
import com.bekircaglar.qarko.presentation.cart.component.PaymentMethodRow
import com.bekircaglar.qarko.presentation.cart.component.PaymentMethodSheet
import com.bekircaglar.qarko.presentation.common.components.BackButton
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.util.toPriceString
import com.bekircaglar.qarko.white
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.arrow_left
import qarko.composeapp.generated.resources.delete

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CartScreen(navController: NavController) {
    val cartItems = remember {
        mutableStateListOf(
            CartItemData(
                imageUrl = "https://images.unsplash.com/photo-1511920170033-f8396924c348", // espresso
                name = "Espresso",
                description = "Güçlü ve yoğun kahve",
                price = 15.90,
                quantity = 1
            ),
            CartItemData(
                imageUrl = "https://images.unsplash.com/photo-1504674900247-0877df9cc836", // cappuccino
                name = "Cappuccino",
                description = "Süt köpüğü ve espresso",
                price = 21.50,
                quantity = 2
            ),
            CartItemData(
                imageUrl = "https://images.unsplash.com/photo-1502741338009-cac2772e18bc", // filtre kahve
                name = "Filtre Kahve",
                description = "Klasik filtre kahve",
                price = 14.00,
                quantity = 1
            ),
        )
    }
    var orderNote by remember { mutableStateOf("") }
    var savedCard by remember { mutableStateOf<CardDetails?>(null) }
    var showPaymentSheet by remember { mutableStateOf(false) }
    val total = remember(cartItems) { cartItems.sumOf { it.price * it.quantity } }
    val modalBottomSheetState = rememberModalBottomSheetState()
    var selectedTabIndex by remember { mutableStateOf(0) }

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
                        color = black
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            cartItems.clear()
                        },
                        modifier = Modifier
                    ) {

                        Icon(
                            painter = painterResource(Res.drawable.delete),
                            contentDescription = "Delete",
                            tint = black,
                            modifier = Modifier.size(16.dp)
                        )

                    }
                },
                navigationIcon = {
                    BackButton(){
                        navController.popBackStack()
                    }
                }
            )
        },
        content = { paddingValues ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(modifier = Modifier.background(white)) {
                        Spacer(modifier = Modifier.height(8.dp))
                        GenericTabRow(
                            tabTitles = listOf("Kart ile öde", "Kasada öde"),
                            selectedTabIndex = selectedTabIndex,
                            onTabSelected = { index ->
                                selectedTabIndex = index
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    when (selectedTabIndex) {
                        0 -> {
                            CardPaymentTab(
                                cartItems = cartItems,
                            )
                        }

                        1 -> {
                            CashPaymentTab(
                                cartItems = cartItems,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .shadow(8.dp)
                        .background(white)
                        .padding(16.dp)
                ) {
                    val total = cartItems.sumOf { it.price * it.quantity }
                    OrderButtonComponent(
                        buttonText = if (selectedTabIndex == 1) "Sipariş Ver" else "Ödemeye Geç",
                        onButtonClick = {  },
                        topContent = {
                            if (selectedTabIndex == 0){
                                PaymentMethodRow(total)
                                Spacer(modifier = Modifier.size(16.dp))
                            } else {
                                if (cartItems.isNotEmpty()) {
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
                        },
                    )
                }
            }
        }
    )

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
