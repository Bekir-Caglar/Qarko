package com.bekircaglar.qarko.presentation.cart.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekircaglar.qarko.data.model.CartItemData
import com.bekircaglar.qarko.gray
import com.bekircaglar.qarko.lighterGray
import com.bekircaglar.qarko.primary
import com.bekircaglar.qarko.white

@Composable

fun CardPaymentTab(
    cartItems: MutableList<CartItemData>,
) {
    Box(
        modifier = Modifier
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
                        price = item.price,
                        description = item.description,
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
                        },
                    )

                    if (index != cartItems.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = lighterGray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(white)
                .fillMaxWidth()

        ) {

            HorizontalDivider(color = lighterGray)

            PaymentSummaryComponent()

            Spacer(modifier = Modifier.size(16.dp))

            Column(
                modifier = Modifier
                    .shadow(8.dp)
                    .background(white)
                    .padding(16.dp)
            ) {

                PaymentMethodRow()

                Spacer(modifier = Modifier.size(16.dp))

                Button(
                    onClick = { /* Handle payment */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primary),
                    enabled = cartItems.isNotEmpty(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Ödeme Yap",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

        }

    }
}
