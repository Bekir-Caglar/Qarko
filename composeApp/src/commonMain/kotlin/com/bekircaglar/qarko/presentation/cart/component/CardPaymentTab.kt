package com.bekircaglar.qarko.presentation.cart.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.bekircaglar.qarko.data.model.CartItemData
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.data.manager.CartManager
import com.bekircaglar.qarko.presentation.cart.CartViewModel
import com.bekircaglar.qarko.presentation.common.theme.black
import com.bekircaglar.qarko.presentation.common.theme.darkPrimary
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.presentation.common.theme.surfaceGray
import com.bekircaglar.qarko.presentation.common.theme.white
import compose.icons.FeatherIcons
import compose.icons.feathericons.Plus
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardPaymentTab(
    cartItems: List<CartItemData>,
    viewModel: CartViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            if (cartItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sepetinizde ürün bulunmamaktadır",
                            fontSize = 16.sp,
                            color = gray
                        )
                    }
                }
            } else {
                itemsIndexed(
                    items = cartItems,
                    key = { _, item -> item.id }
                ) { _, item ->
                    CartItem(
                        imageUrl = item.imageUrl,
                        name = item.name,
                        price = item.price,
                        description = item.description,
                        quantity = item.quantity,
                        onIncreaseQuantity = {
                            CartManager.updateQuantity(item.id, item.quantity + 1)
                            viewModel.loadUpsellRecommendations()
                        },
                        onDecreaseQuantity = {
                            CartManager.updateQuantity(item.id, item.quantity - 1)
                            viewModel.loadUpsellRecommendations()
                        },
                        onRemove = {
                            CartManager.removeFromCart(item.id)
                            viewModel.loadUpsellRecommendations()
                        },
                    )
                }

                // Yanında İyi Gider Bölümü (Sadece öneri varsa göster)
                if (uiState.recommendations.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(white)
                                .padding(vertical = 20.dp)
                        ) {
                            // Başlık
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✨",
                                    fontSize = 24.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Yanında İyi Gider",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = black
                                    )
                                    Text(
                                        text = "Bu lezzetleri kaçırma",
                                        fontSize = 12.sp,
                                        color = gray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Öneri listesi (Yatay Kaydırılabilir)
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(uiState.recommendations) { item ->
                                    SuggestionItemCard(
                                        item = item,
                                        onAddClick = {
                                            val price = item.price.replace("₺", "").replace(",", ".").replace(" ", "").toDoubleOrNull() ?: 0.0
                                            CartManager.addToCart(
                                                foodItem = item,
                                                quantity = 1,
                                                selectedSingleOptions = emptyMap(),
                                                selectedMultiOptions = emptyMap(),
                                                removedItems = emptySet(),
                                                totalPrice = price
                                            )
                                            viewModel.loadUpsellRecommendations() // Sepet güncellendiği için önerileri tazele
                                        }
                                    )
                                }
                            }
                        }
                    }
                } else if (uiState.isRecommendationsLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = primary, modifier = Modifier.size(32.dp))
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun SuggestionItemCard(
    item: FoodItem,
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .background(surfaceGray, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            // Hızlı Ekle Butonu
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .size(28.dp)
                    .background(primary, CircleShape)
                    .clip(CircleShape)
                    .clickable(onClick = onAddClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = FeatherIcons.Plus,
                    contentDescription = "Ekle",
                    tint = white,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = item.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = item.info,
            fontSize = 11.sp,
            color = gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 14.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.price,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = primary
        )
    }
}
