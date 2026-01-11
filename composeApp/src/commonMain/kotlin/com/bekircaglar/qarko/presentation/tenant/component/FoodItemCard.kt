package com.bekircaglar.qarko.presentation.tenant.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.data.manager.CartManager
import com.bekircaglar.qarko.data.manager.FavoritesManager
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.*
import compose.icons.FeatherIcons
import compose.icons.feathericons.Clock
import compose.icons.feathericons.Heart
import compose.icons.feathericons.Plus

/**
 * Compact Food Item Card - Similar to reference design
 */
@Composable
fun FoodItemCard(
    item: FoodItem,
    onClick: () -> Unit
) {
    // Sepetteki bu ürünün miktarını CartManager'dan al
    val cartQuantity by remember {
        derivedStateOf {
            CartManager.cartItems.filter { it.foodId == item.id }.sumOf { it.quantity }
        }
    }

    // Favori durumunu FavoritesManager'dan al
    val isFavorite by remember {
        derivedStateOf {
            FavoritesManager.isFavorite(item.id)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Main content row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left: Image with badges
                Box(modifier = Modifier.size(90.dp)) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )

                    // Top-left badge: "YENİ" or discount badge
                    if (item.isNew || item.discountPercent > 0) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(4.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = if (item.discountPercent > 0) primary else Color(0xFF4CAF50)
                        ) {
                            QText(
                                text = if (item.discountPercent > 0) "-%${item.discountPercent}" else "YENİ",
                                color = white,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Cart quantity badge (bottom-right of image)
                    if (cartQuantity > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(4.dp)
                                .size(20.dp)
                                .shadow(2.dp, CircleShape)
                                .background(primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            QText(
                                text = cartQuantity.toString(),
                                color = white,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Right: Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top section
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Name
                        Spacer(modifier = Modifier.height(1.dp))
                        QText(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = darkBlue,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(end = 32.dp) // Space for heart icon
                        )

                        // Info row: Rating, Time, Calories
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Rating
                            if (item.rating > 0) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = yellow,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    QText(
                                        text = item.rating.toString().take(3),
                                        color = darkGray,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            QText(text = "•", color = gray, fontSize = 11.sp)

                            // Prep time
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = FeatherIcons.Clock,
                                    contentDescription = null,
                                    tint = gray,
                                    modifier = Modifier.size(11.dp)
                                )
                                QText(
                                    text = "${item.prepTime} dk",
                                    color = darkGray,
                                    fontSize = 11.sp
                                )
                            }

                            QText(text = "•", color = gray, fontSize = 11.sp)

                            // Calories
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                QText(text = "🔥", fontSize = 10.sp)
                                QText(
                                    text = "${item.calories} kcal",
                                    color = darkGray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Bottom: Price and Add Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        QText(
                            text = item.price,
                            color = primary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )

                        Surface(
                            onClick = { onClick() },
                            shape = RoundedCornerShape(12.dp),
                            color = primary.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = FeatherIcons.Plus,
                                    contentDescription = "Ekle",
                                    tint = primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Heart icon at top-right - Toggle favorite
            Surface(
                onClick = {
                    FavoritesManager.toggleFavorite(item)
                },
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(36.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else FeatherIcons.Heart,
                        contentDescription = if (isFavorite) "Favorilerden çıkar" else "Favorilere ekle",
                        tint = if (isFavorite) Color(0xFFE91E63) else gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
