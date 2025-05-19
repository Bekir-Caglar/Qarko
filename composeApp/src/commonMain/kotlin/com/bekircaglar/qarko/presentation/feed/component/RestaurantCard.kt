package com.bekircaglar.qarko.presentation.feed.component
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.bekircaglar.qarko.data.model.Restaurant

//@Composable
//fun RestaurantCard(restaurant: Restaurant) {
//    var isFavorite by remember { mutableStateOf(false) }
//    val context = LocalPlatformContext.current
//
//    Card(
//        shape = RoundedCornerShape(16.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//        colors = CardDefaults.cardColors(containerColor = white),
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Box {
//            Row(modifier = Modifier.height(120.dp)) {
//                Box(
//                    modifier = Modifier
//                        .width(140.dp)
//                        .fillMaxHeight()
//                ) {
//                    SubcomposeAsyncImage(
//                        model =  ImageRequest.Builder(context)
//                            .data(restaurant.imageUrl)
//                            .crossfade(true)
//                            .build(),
//                        contentDescription = "${restaurant.name} image",
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier.fillMaxSize()
//                    )
//
//                    Row(
//                        modifier = Modifier
//                            .padding(4.dp)
//                            .align(Alignment.TopStart)
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .clip(RoundedCornerShape(6.dp))
//                                .background(black.copy(alpha = 0.5f))
//                                .padding(horizontal = 6.dp, vertical = 2.dp),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text(
//                                text = restaurant.deliveryTime,
//                                color = Color.White,
//                                fontSize = 10.sp,
//                                fontWeight = FontWeight.Medium
//                            )
//                        }
//                    }
//                }
//
//                Column(
//                    modifier = Modifier
//                        .fillMaxHeight()
//                        .padding(12.dp)
//                        .weight(1f)
//                ) {
//                    Text(
//                        text = restaurant.name,
//                        fontSize = 16.sp,
//                        fontWeight = Bold,
//                        color = black,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//
//                    Spacer(modifier = Modifier.height(4.dp))
//
//                    Text(
//                        text = restaurant.categories,
//                        fontSize = 14.sp,
//                        color = gray,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//
//                    Spacer(modifier = Modifier.weight(1f))
//
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Icon(
//                                imageVector = Icons.Default.Star,
//                                contentDescription = "Rating",
//                                tint = yellow,
//                                modifier = Modifier.size(18.dp)
//                            )
//
//                            Spacer(modifier = Modifier.width(4.dp))
//
//                            Text(
//                                text = restaurant.rating.toString(),
//                                fontSize = 14.sp,
//                                fontWeight = FontWeight.Medium,
//                                color = darkGray
//                            )
//
//                            Spacer(modifier = Modifier.width(4.dp))
//
//                            Text(
//                                text = "(${restaurant.reviewCount})",
//                                fontSize = 14.sp,
//                                color = gray
//                            )
//                        }
//
//                        Text(
//                            text = restaurant.distance,
//                            fontSize = 14.sp,
//                            color = gray
//                        )
//                    }
//                }
//            }
//
//            // Favori butonu - kartın sağ üstüne yerleştirme
//            IconButton(
//                onClick = { isFavorite = !isFavorite },
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .padding(8.dp)
//                    .size(32.dp)
//                    .clip(CircleShape)
//            ) {
//                Icon(
//                    imageVector = if (isFavorite) {
//                        Icons.Filled.Favorite
//                    } else {
//                        Icons.Filled.FavoriteBorder
//                    },
//                    contentDescription = "Favori",
//                    tint = if (isFavorite) Color.Red else Color.Gray,
//                    modifier = Modifier.size(20.dp)
//                )
//            }
//        }
//    }
//}




@Composable
fun RestaurantCard(restaurant: Restaurant, onCardSelected:() -> Unit = {}, modifier: Modifier = Modifier) {

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.fillMaxWidth(),
        onClick = { onCardSelected() }
    ) {
        Column {
            // Image at the top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                SubcomposeAsyncImage(
                    model = restaurant.imageUrl,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFEEEEEE)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFFfb7433),
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    contentDescription = "${restaurant.name} image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x99000000))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = restaurant.deliveryTime,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x99000000))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = restaurant.distance,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Details section below the image
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = restaurant.name,
                        fontSize = 18.sp,
                        fontWeight = Bold,
                        color = Color(0xFF181d2f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )


                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = restaurant.categories,
                    fontSize = 14.sp,
                    color = Color(0xFF979797),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = restaurant.rating.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF181d2f)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "(${restaurant.reviewCount} reviews)",
                        fontSize = 14.sp,
                        color = Color(0xFF979797)
                    )
                }
            }
        }
    }
}