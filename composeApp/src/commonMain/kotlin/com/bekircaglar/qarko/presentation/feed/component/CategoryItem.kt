package com.bekircaglar.qarko.presentation.feed.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.bekircaglar.qarko.black
import com.bekircaglar.qarko.darkGray
import com.bekircaglar.qarko.data.model.FoodCategory
import com.bekircaglar.qarko.gray
import com.bekircaglar.qarko.white

@Composable
fun CategoryItem(category: FoodCategory) {

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        modifier = Modifier
            .width(120.dp)
            .height(140.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                AsyncImage(
                    model = category.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column {
                    Text(
                        text = category.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Starting",
                            fontSize = 12.sp,
                            color = gray,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = category.startingPrice,
                            fontSize = 14.sp,
                            fontWeight = Bold,
                            color = darkGray,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                        )
                    }
                }


            }
        }
    }
}