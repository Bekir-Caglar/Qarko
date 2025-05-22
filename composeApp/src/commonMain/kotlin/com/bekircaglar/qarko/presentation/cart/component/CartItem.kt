package com.bekircaglar.qarko.presentation.cart.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.bekircaglar.qarko.black
import com.bekircaglar.qarko.darkBlue
import com.bekircaglar.qarko.gray
import com.bekircaglar.qarko.lighterGray
import com.bekircaglar.qarko.white
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.add
import qarko.composeapp.generated.resources.delete
import qarko.composeapp.generated.resources.minus

@Composable
fun CartItem(
    imageUrl: String,
    name: String,
    description: String,
    price: Double,
    quantity: Int,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    onRemove: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = name,
            modifier = Modifier
                .height(70.dp)
                .width(70.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = black
            )

            Text(
                text = description,
                fontSize = 13.sp,
                color = gray
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "${price} TL",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = darkBlue
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDecreaseQuantity,
                modifier = Modifier
                    .size(24.dp)
            ) {
                Icon(
                    painter = painterResource(if (quantity > 1) Res.drawable.minus else Res.drawable.delete),
                    contentDescription = "Azalt",
                    tint = black,
                    modifier = Modifier.size(12.dp)
                )
            }

            Text(
                text = quantity.toString(),
                modifier = Modifier.padding(horizontal = 8.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            IconButton(
                onClick = onIncreaseQuantity,
                modifier = Modifier
                    .size(24.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.add),
                    contentDescription = "Artır",
                    tint = black,
                    modifier = Modifier.size(12.dp)
                )
            }
        }


    }

}