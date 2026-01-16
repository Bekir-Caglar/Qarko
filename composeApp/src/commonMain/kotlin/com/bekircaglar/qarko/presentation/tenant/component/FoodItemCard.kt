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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
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
import compose.icons.feathericons.Percent
import compose.icons.feathericons.Zap

@Composable
fun FoodItemCard(
    item: FoodItem,
    onClick: () -> Unit
) {
    val cartQuantity by remember {
        derivedStateOf {
            CartManager.cartItems.filter { it.foodId == item.id }.sumOf { it.quantity }
        }
    }

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sol taraf: Resim
                Box(modifier = Modifier.width(110.dp).height(90.dp)) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                            .zIndex(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (item.isFeatured) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFFA000)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(Icons.Default.Star, null, tint = white, modifier = Modifier.size(10.dp))
                                    QText(text = "Öne Çıkan", color = white, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (item.discountPercent > 0) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFD32F2F)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(FeatherIcons.Percent, null, tint = white, modifier = Modifier.size(10.dp))
                                    QText(text = "%${item.discountPercent} İndirim", color = white, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (cartQuantity > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(4.dp)
                                .size(22.dp)
                                .shadow(2.dp, CircleShape)
                                .background(primary, CircleShape)
                                .zIndex(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            QText(text = cartQuantity.toString(), color = white, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Sağ taraf: İçerik
                Column(
                    modifier = Modifier.weight(1f).height(90.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Üst kısım (İsim ve Açıklama) - Butonların altında kalmaması için padding eklendi
                    Column(
                        modifier = Modifier.padding(end = 36.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        QText(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = darkBlue,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        QText(
                            text = item.info,
                            color = gray,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!item.prepTime.isNullOrEmpty()) {
                                Row(
                                    modifier = Modifier.background(lighterGray, RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(FeatherIcons.Clock, null, tint = gray, modifier = Modifier.size(11.dp))
                                    QText(text = "${item.prepTime} dk", color = gray, fontSize = 10.sp)
                                }
                            }

                            if (item.calories > 0) {
                                Row(
                                    modifier = Modifier.background(lighterGray, RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(FeatherIcons.Zap, null, tint = gray, modifier = Modifier.size(11.dp))
                                    QText(text = "${item.calories} kcal", color = gray, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    // Alt kısım (Fiyat) - Ekle butonuyla çakışmaması için padding eklendi
                    Column(
                        modifier = Modifier.padding(end = 36.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        if (item.originalPrice != null) {
                            QText(
                                text = item.originalPrice!!,
                                color = gray,
                                fontSize = 12.sp,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                        QText(
                            text = item.price,
                            color = primary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            // Favori Butonu
            Surface(
                onClick = { FavoritesManager.toggleFavorite(item) },
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent,
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else FeatherIcons.Heart,
                        contentDescription = null,
                        tint = if (isFavorite) Color(0xFFE91E63) else gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Ekle Butonu
            Surface(
                onClick = { onClick() },
                shape = RoundedCornerShape(12.dp),
                color = primary.copy(alpha = 0.12f),
                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp).size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = FeatherIcons.Plus,
                        contentDescription = null,
                        tint = primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
