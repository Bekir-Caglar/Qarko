package com.bekircaglar.qarko.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekircaglar.qarko.navigation.FoodDetail
import com.bekircaglar.qarko.navigation.Checkout
import com.bekircaglar.qarko.presentation.common.theme.black
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.presentation.common.theme.lightGray
import com.bekircaglar.qarko.data.manager.CartManager
import com.bekircaglar.qarko.data.manager.FavoritesManager
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.presentation.cart.component.CardPaymentTab
import com.bekircaglar.qarko.presentation.cart.component.PaymentMethodSheet
import com.bekircaglar.qarko.presentation.common.components.BackButton
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.presentation.common.theme.surfaceGray
import com.bekircaglar.qarko.presentation.common.theme.white
import coil3.compose.AsyncImage
import com.bekircaglar.qarko.presentation.common.theme.lighterGray
import compose.icons.FeatherIcons
import compose.icons.feathericons.ShoppingCart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController) {
    val cartItems = CartManager.cartItems

    var showPaymentSheet by remember { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState()

    // Calculate total reactively
    val total by remember {
        derivedStateOf {
            cartItems.sumOf { it.price * it.quantity }
        }
    }

    Scaffold(
        containerColor = surfaceGray,
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
                val favoriteItems = FavoritesManager.getCurrentTenantFavorites()

                // Empty cart state
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = FeatherIcons.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(bottom = 16.dp),
                            tint = lightGray
                        )

                        Text(
                            text = "Sepetiniz Boş",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = black
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Henüz sepetinize bir şey eklemediniz.",
                            fontSize = 16.sp,
                            color = gray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primary
                            )
                        ) {
                            Text(
                                text = "Menüyü Görüntüle",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(
                            onClick = { /* TODO: Navigate to previous orders */ }
                        ) {
                            Text(
                                text = "Önceki Siparişlerim",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = primary
                            )
                        }
                    }

                    if (favoriteItems.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                        ) {
                            Text(
                                text = "Favorileriniz",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = black
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(favoriteItems) { item ->
                                    FavoriteItemCard(
                                        item = item,
                                        onClick = { navController.navigate(FoodDetail.fromFoodItem(item)) }
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Card Payment Content (tab kaldırıldı)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        CardPaymentTab(cartItems = cartItems)
                    }

                    // Bottom payment section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(white)
                            .padding(16.dp)
                    ) {
                        val deliveryFee = 15.0
                        val subTotal = total
                        val discount = 20.0
                        val finalTotal = subTotal + deliveryFee - discount
                        val originalTotal = subTotal + deliveryFee

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(primary),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Button part
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .clickable { navController.navigate(Checkout) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Ödemeye Geç",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = white
                                )
                            }

                            // Price part - continuation of button
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .background(lighterGray)
                                    .padding(horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "₺${originalTotal.toInt()}",
                                    fontSize = 14.sp,
                                    color = gray,
                                    fontWeight = FontWeight.Medium,
                                    style = androidx.compose.ui.text.TextStyle(
                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                    )
                                )
                                Text(
                                    text = "₺${finalTotal.toInt()}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = black
                                )
                            }
                        }
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
                    // Handle card save
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

@Composable
fun FavoriteItemCard(
    item: FoodItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(110.dp)
            .background(white, RoundedCornerShape(12.dp))
            .border(1.dp, lightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.name,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "₺${item.price}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = primary
        )
    }
}
