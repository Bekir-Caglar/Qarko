package com.bekircaglar.qarko.presentation.cart.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekircaglar.qarko.darkBlue
import com.bekircaglar.qarko.data.model.CartItemData
import com.bekircaglar.qarko.gray
import com.bekircaglar.qarko.lighterGray
import com.bekircaglar.qarko.primary
import com.bekircaglar.qarko.white

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