package com.bekircaglar.qarko.presentation.cart.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekircaglar.qarko.data.model.CartItemData
import com.bekircaglar.qarko.data.manager.CartManager
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.presentation.common.theme.lighterGray
import com.bekircaglar.qarko.presentation.common.theme.white

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardPaymentTab(
    cartItems: List<CartItemData>,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 160.dp) // Ödeme bölümü için bottom padding
        ) {
            if (cartItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sepetinizde ürün bulunmamaktadır",
                            fontSize = 16.sp,
                            color = gray
                        )
                    }
                }
            } else {
                itemsIndexed(
                    items = cartItems,
                    key = { _, item -> item.id } // Benzersiz ID kullan
                ) { index, item ->
                    CartItem(
                        imageUrl = item.imageUrl,
                        name = item.name,
                        price = item.price,
                        description = item.description,
                        quantity = item.quantity,
                        onIncreaseQuantity = {
                            CartManager.updateQuantity(item.id, item.quantity + 1)
                        },
                        onDecreaseQuantity = {
                            CartManager.updateQuantity(item.id, item.quantity - 1)
                        },
                        onRemove = {
                            CartManager.removeFromCart(item.id)
                        },
                    )

                    if (index < cartItems.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = lighterGray
                        )
                    }
                }

                // Son öğeden sonra biraz boşluk
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Alt kısımdaki içerik (gerekli değilse kaldırabilirsiniz)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(white)
                .fillMaxWidth()
        ) {
            // Bu kısım artık CartScreen'de kontrol edildiği için boş bırakıldı
        }
    }
}