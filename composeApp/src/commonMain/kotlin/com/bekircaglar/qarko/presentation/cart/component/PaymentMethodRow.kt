package com.bekircaglar.qarko.presentation.cart.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bekircaglar.qarko.black
import com.bekircaglar.qarko.gray
import com.bekircaglar.qarko.util.toPriceString
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.credit_card
import qarko.composeapp.generated.resources.mastercard

@Composable
fun PaymentMethodRow(
    totalPrice: Double
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {}
        ) {
            Image(
                painter = painterResource(Res.drawable.mastercard),
                contentDescription = "Payment Method Icon",
                modifier = Modifier.size(24.dp)
            )

            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = "Ziraat kartım",
                    fontSize = 16.sp,
                    color = black,
                )

                Text(
                    text = "**** **** **** 6518",
                    fontSize = 14.sp,
                    color = Color.Gray,
                )

            }

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Arrow Drop Down",
                tint = Color.Gray,
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

       Row(
           verticalAlignment = Alignment.CenterVertically
       ) {
           Text(
               text = (totalPrice + totalPrice*0.4).toPriceString(),
               fontSize = 16.sp,
               color = gray,
               style = TextStyle(textDecoration = TextDecoration.LineThrough),
               modifier = Modifier.padding(end = 4.dp)
           )
           Text(
               text = totalPrice.toPriceString(),
               fontSize = 16.sp,
               fontWeight = Bold,
               color = Color.Black,
               modifier = Modifier.padding(end = 8.dp)
           )
       }
    }

}
