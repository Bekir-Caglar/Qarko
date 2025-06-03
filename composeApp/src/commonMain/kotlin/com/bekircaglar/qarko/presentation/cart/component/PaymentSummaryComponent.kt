package com.bekircaglar.qarko.presentation.cart.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekircaglar.qarko.darkBlue
import com.bekircaglar.qarko.darkPrimary
import com.bekircaglar.qarko.gray

@Composable
fun PaymentSummaryComponent(
    price: Double = 4.53,
    originalDeliveryFee: Double = 2.0,
    discountedDeliveryFee: Double = 1.0,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sipariş Özeti",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Price
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ürün Tutarı",
                fontSize = 16.sp,
                color = gray
            )
            Text(
                text = "$price",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = gray
            )


        }


        Spacer(modifier = Modifier.height(8.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Bahşiş",
                fontSize = 16.sp,
                color = gray
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$originalDeliveryFee",
                    fontSize = 16.sp,
                    color = gray,
                    style = TextStyle(textDecoration = TextDecoration.LineThrough),
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = "$discountedDeliveryFee",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Toplam",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = darkBlue
            )
            val total = price + discountedDeliveryFee
            Text(
                text = "₺$total",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = darkBlue
            )
        }
    }
}
