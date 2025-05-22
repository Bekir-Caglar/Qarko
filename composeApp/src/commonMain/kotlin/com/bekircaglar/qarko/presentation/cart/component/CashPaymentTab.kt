package com.bekircaglar.qarko.presentation.cart.component

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekircaglar.qarko.darkGreen
import com.bekircaglar.qarko.data.model.CartItemData
import com.bekircaglar.qarko.gray
import com.bekircaglar.qarko.lightGray
import com.bekircaglar.qarko.primary
import com.bekircaglar.qarko.white


@Composable
fun CashPaymentTab(cartItems : MutableList<CartItemData>) {

    var orderNote by remember { mutableStateOf("") }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(top = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sepetinizde ürün bulunmamaktadır",
                        fontSize = 16.sp,
                        color = gray
                    )
                }
            } else {
                cartItems.forEachIndexed { index, item ->
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
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = white)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Kasada Ödeme",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Siparişinizi şimdi verin, ödemeyi kasada yapın.",
                            fontSize = 14.sp,
                            color = gray
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .shadow(8.dp)
                .background(white)
                .padding(16.dp)
        ) {
            if (cartItems.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Toplam",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "₺123.45",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkGreen
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = { /* Handle order placement */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primary),
                enabled = cartItems.isNotEmpty(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Sipariş Ver",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}