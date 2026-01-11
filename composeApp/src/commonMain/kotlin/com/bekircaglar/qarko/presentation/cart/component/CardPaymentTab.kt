package com.bekircaglar.qarko.presentation.cart.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.bekircaglar.qarko.data.model.FoodType
import com.bekircaglar.qarko.data.manager.CartManager
import com.bekircaglar.qarko.presentation.common.theme.black
import com.bekircaglar.qarko.presentation.common.theme.darkPrimary
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.presentation.common.theme.lightGray
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.presentation.common.theme.surfaceGray
import com.bekircaglar.qarko.presentation.common.theme.white
import compose.icons.FeatherIcons
import compose.icons.feathericons.Plus

// Öneri kategorileri
private data class SuggestionCategory(
    val name: String,
    val emoji: String,
    val items: List<FoodItem>
)

// Örnek öneri verileri - gerçek uygulamada API'den gelecek
private fun getSuggestionCategories(): List<SuggestionCategory> {
    return listOf(
        SuggestionCategory(
            name = "İçecekler",
            emoji = "🥤",
            items = listOf(
                FoodItem(
                    id = "sug_drink1",
                    name = "Limonata",
                    imageUrl = "https://images.unsplash.com/photo-1621263764928-df1444c5e859?q=80&w=400&fit=crop",
                    price = "₺35",
                    info = "Taze sıkılmış",
                    category = "İçecekler",
                    foodType = FoodType.DRINK
                ),
                FoodItem(
                    id = "sug_drink2",
                    name = "Cola",
                    imageUrl = "https://images.unsplash.com/photo-1596803244897-c7dec84a551e?q=80&w=400&fit=crop",
                    price = "₺20",
                    info = "330ml",
                    category = "İçecekler",
                    foodType = FoodType.DRINK
                ),
                FoodItem(
                    id = "sug_drink3",
                    name = "Ayran",
                    imageUrl = "https://images.unsplash.com/photo-1596803244897-c7dec84a551e?q=80&w=400&fit=crop",
                    price = "₺15",
                    info = "Ev yapımı",
                    category = "İçecekler",
                    foodType = FoodType.DRINK
                )
            )
        ),
        SuggestionCategory(
            name = "Tatlılar",
            emoji = "🍰",
            items = listOf(
                FoodItem(
                    id = "sug_dessert1",
                    name = "Künefe",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺80",
                    info = "Antep fıstıklı",
                    category = "Tatlılar",
                    foodType = FoodType.DESSERT
                ),
                FoodItem(
                    id = "sug_dessert2",
                    name = "Sütlaç",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺45",
                    info = "Fırın sütlaç",
                    category = "Tatlılar",
                    foodType = FoodType.DESSERT
                ),
                FoodItem(
                    id = "sug_dessert3",
                    name = "Baklava",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺120",
                    info = "6 dilim",
                    category = "Tatlılar",
                    foodType = FoodType.DESSERT
                )
            )
        ),
        SuggestionCategory(
            name = "Sıcak İçecekler",
            emoji = "☕",
            items = listOf(
                FoodItem(
                    id = "sug_hot1",
                    name = "Türk Kahvesi",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺25",
                    info = "Orta şekerli",
                    category = "Sıcak İçecekler",
                    foodType = FoodType.HOT_DRINK
                ),
                FoodItem(
                    id = "sug_hot2",
                    name = "Çay",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺15",
                    info = "Demlik çay",
                    category = "Sıcak İçecekler",
                    foodType = FoodType.HOT_DRINK
                )
            )
        )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardPaymentTab(
    cartItems: List<CartItemData>,
) {
    val suggestionCategories = remember { getSuggestionCategories() }
    var selectedTabIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
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
                ) { index, item ->
                    CartItem(
                        imageUrl = item.imageUrl,
                        name = item.name,
                        price = item.price,
                        description = item.description,
                        quantity = item.quantity,
                        onIncreaseQuantity = {
                            CartManager.updateQuantity(item.id, item.quantity + 1)
                        },
                        onDecreaseQuantity = {
                            CartManager.updateQuantity(item.id, item.quantity - 1)
                        },
                        onRemove = {
                            CartManager.removeFromCart(item.id)
                        },
                    )
                }

                // Yanında İyi Gider Bölümü
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(white)
                            .padding(vertical = 16.dp)
                    ) {
                        // Başlık
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🍽️",
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Yanında İyi Gider",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = black
                                )
                                Text(
                                    text = "Siparişini tamamla",
                                    fontSize = 12.sp,
                                    color = gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Tab menü
                        ScrollableTabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = white,
                            contentColor = primary,
                            edgePadding = 16.dp,
                            indicator = { tabPositions ->
                                if (tabPositions.isNotEmpty()) {
                                    TabRowDefaults.SecondaryIndicator(
                                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                        height = 3.dp,
                                        color = primary
                                    )
                                }
                            },
                            divider = {}
                        ) {
                            suggestionCategories.forEachIndexed { index, category ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = category.emoji,
                                            fontSize = 16.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = category.name,
                                            fontSize = 14.sp,
                                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                            color = if (selectedTabIndex == index) primary else gray
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Seçili kategorinin ürünleri
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(suggestionCategories[selectedTabIndex].items) { item ->
                                SuggestionItemCard(
                                    item = item,
                                    onAddClick = {
                                        // Sepete ekle
                                        val price = item.price.replace("₺", "").replace(",", ".").toDoubleOrNull() ?: 0.0
                                        CartManager.addToCart(
                                            foodItem = item,
                                            quantity = 1,
                                            selectedSingleOptions = emptyMap(),
                                            selectedMultiOptions = emptyMap(),
                                            removedItems = emptySet(),
                                            totalPrice = price
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(white)
                .fillMaxWidth()
        ) {
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
            .width(130.dp)
            .background(surfaceGray, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Ekle butonu
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
                    .background(primary, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onAddClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = FeatherIcons.Plus,
                    contentDescription = "Ekle",
                    tint = white,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = item.info,
            fontSize = 11.sp,
            color = gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.price,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = darkPrimary
        )
    }
}
