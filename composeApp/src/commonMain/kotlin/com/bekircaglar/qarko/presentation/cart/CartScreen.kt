package com.bekircaglar.qarko.presentation.cart

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekircaglar.qarko.black
import com.bekircaglar.qarko.darkBlue
import com.bekircaglar.qarko.darkGreen
import com.bekircaglar.qarko.data.model.CartItemData
import com.bekircaglar.qarko.gray
import com.bekircaglar.qarko.lightGray
import com.bekircaglar.qarko.lighterGray
import com.bekircaglar.qarko.navigation.Screen
import com.bekircaglar.qarko.presentation.cart.component.CardDetails
import com.bekircaglar.qarko.presentation.cart.component.CardPaymentTab
import com.bekircaglar.qarko.presentation.cart.component.CartItem
import com.bekircaglar.qarko.presentation.cart.component.CartTabRow
import com.bekircaglar.qarko.presentation.cart.component.CashPaymentTab
import com.bekircaglar.qarko.presentation.cart.component.OrderButtonComponent
import com.bekircaglar.qarko.presentation.cart.component.PaymentMethodRow
import com.bekircaglar.qarko.presentation.cart.component.PaymentMethodSheet
import com.bekircaglar.qarko.presentation.cart.component.PaymentSummaryComponent
import com.bekircaglar.qarko.presentation.cart.component.TableEntryCard
import com.bekircaglar.qarko.presentation.common.components.BackButton
import com.bekircaglar.qarko.primary
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
                price = 180000.50,
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
                    Text(
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
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                        modifier = Modifier
                    ) {

                        Icon(
                            painter = painterResource(Res.drawable.arrow_left),
                            contentDescription = "back",
                            tint = black,
                            modifier = Modifier.size(16.dp)
                        )

                    }
                }
            )
        },
        content = { paddingValues ->

            var selectedTable by remember { mutableStateOf<String?>(null) }
            var restaurantName by remember { mutableStateOf<String?>(null) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Apply padding from Scaffold
            ) {
                // Kaydırılabilir içerik alanı
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                    // Sabit ödeme bölümü için altta boşluk bırakın
                ) {
                    Column(modifier = Modifier.background(white)) { // Arka planı beyaz yaparak alttaki öğelerin görünmesini engelle
                        TableEntryCard(
                            tableNumber = selectedTable ?: "-", // Null durumunda gösterilecek metin
                            isTableActive = selectedTable != null,
                            restaurantName = restaurantName,
                            onQrScanClick = {
                                selectedTable = "15" // QR'dan gelen masa numarası
                                restaurantName = "Lezzet Restaurant" // QR'dan gelen restoran adı
                            },
                            onChangeTableClick = {
                                selectedTable = null
                                restaurantName = null
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // TableEntryCard sonrası boşluk
                        CartTabRow(
                            selectedTabIndex = selectedTabIndex,
                            onTabSelected = { selectedTabIndex = it }
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // CartTabRow sonrası boşluk
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
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter) // Bu Column'u parent Box'ın altına hizala
                        .shadow(8.dp)
                        .background(white)
                        .padding(16.dp)
                ) {

                    OrderButtonComponent(
                        buttonText = if (selectedTabIndex == 1) "Sipariş Ver" else "Ödemeye Geç",
                        isButtonEnabled = cartItems.isNotEmpty() && selectedTable != null,
                        onButtonClick = { /* Sipariş verme işlemini yönet */ },
                        topContent = {
                            if (selectedTabIndex == 0){
                                PaymentMethodRow(120.0)
                                Spacer(modifier = Modifier.size(16.dp))
                            } else {
                                if (cartItems.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Toplam",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )

                                        Text(
                                            text = "₺120", // Toplam tutarı formatla
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = darkBlue
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        },
                        showWarning = cartItems.isNotEmpty() && selectedTable == null,
                        warningText = "Lütfen sipariş vermek için önce bir masa seçin."
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