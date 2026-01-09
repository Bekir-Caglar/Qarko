package com.bekircaglar.qarko.presentation.cart.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.presentation.common.theme.lighterGray

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CashPaymentTab(cartItems: MutableList<CartItemData>) {


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 160.dp)
        ) {

            if (cartItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth() // LazyColumn içinde fillMaxWidth yerine kullanılır
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
                    key = { _, item -> item.name } // Benzersiz bir anahtar kullanın (örneğin ürün ID'si)
                ) { index, item ->
                    CartItem(
                        imageUrl = item.imageUrl,
                        name = item.name,
                        description = item.description,
                        price = item.price,
                        quantity = item.quantity,
                        onIncreaseQuantity = {
                            cartItems[index] =
                                item.copy(quantity = item.quantity + 1)
                        },
                        onDecreaseQuantity = {
                            if (item.quantity > 1) {
                                cartItems[index] =
                                    item.copy(quantity = item.quantity - 1)
                            } else {
                                cartItems.removeAt(index)
                            }
                        },
                        onRemove = {
                            cartItems.removeAt(index)
                        }
                    )

                    if (index < cartItems.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = lighterGray
                        )
                    }
                }
            }
        }

    }
}