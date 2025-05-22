package com.bekircaglar.qarko.presentation.cart.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.credit_card

@Composable
fun PaymentMethodRow() {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(
                RoundedCornerShape(8.dp)
            )
            .clickable {

            }
    ) {
        Icon(
            painterResource(Res.drawable.credit_card),
            contentDescription = "Payment Method Icon",
            modifier = Modifier.size(24.dp)
        )

        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = "Credit Card",
                fontSize = 16.sp,
                color = Color.Black,
            )

            Text(
                text = "**** **** **** 1234",
                fontSize = 14.sp,
                color = Color.Gray,
            )

        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Arrow Drop Down",
            tint = Color.Gray,
        )
    }

}