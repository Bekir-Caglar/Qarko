package com.bekircaglar.qarko.presentation.tenant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil3.compose.AsyncImage
import com.bekircaglar.qarko.darkBlue
import com.bekircaglar.qarko.darkPrimary
import com.bekircaglar.qarko.data.model.Allergen
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.gray
import com.bekircaglar.qarko.lightGray
import com.bekircaglar.qarko.navigation.AppBottomBar
import com.bekircaglar.qarko.navigation.Screen
import com.bekircaglar.qarko.presentation.common.components.BackButton
import com.bekircaglar.qarko.presentation.feed.component.SearchTextField
import com.bekircaglar.qarko.presentation.tenant.component.FoodItemCard
import com.bekircaglar.qarko.primary
import com.bekircaglar.qarko.surfaceGray
import com.bekircaglar.qarko.white
import com.bekircaglar.qarko.yellow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.arrow_left
import qarko.composeapp.generated.resources.clock
import qarko.composeapp.generated.resources.favourite
import qarko.composeapp.generated.resources.ifsokak_logo
import qarko.composeapp.generated.resources.location
import qarko.composeapp.generated.resources.more_horizontal
import qarko.composeapp.generated.resources.qr
import qarko.composeapp.generated.resources.shopping_cart

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TenantMenuScreen(navController: NavController) {
    val menuCategories = listOf(
        "Favoriler",
        "Başlangıçlar",
        "Pizzalar",
        "Burgerler",
        "Salatalar",
        "İçecekler",
        "Tatlılar",
        "Yan Ürünler"
    )

    val categorizedFoods = mapOf(
        "Favoriler" to listOf(
            FoodItem(
                name = "Margherita Pizza",
                imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?q=80&w=400&fit=crop",
                price = "₺120",
                info = "Mozzarella, domates, fesleğen",
                category = "Favoriler",
                allergens = commonAllergens
            ),
            FoodItem(
                name = "Cheeseburger",
                imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?q=80&w=400&fit=crop",
                price = "₺95",
                info = "Dana eti, cheddar, turşu",
                category = "Favoriler",
                allergens = commonAllergens
            )
        ),
        "Başlangıçlar" to listOf(
            FoodItem(
                name = "Bruschetta",
                imageUrl = "https://images.unsplash.com/photo-1506280754576-f6fa8a873550?q=80&w=400&fit=crop",
                price = "₺75",
                info = "Domates, sarımsak, fesleğen",
                category = "Başlangıçlar"
            ),
            FoodItem(
                name = "Sarımsaklı Ekmek",
                imageUrl = "https://images.unsplash.com/photo-1573140247632-f8fd74997d5c?q=80&w=400&fit=crop",
                price = "₺60",
                info = "Sarımsak, tereyağı, maydanoz",
                category = "Başlangıçlar"
            )
        ),
        "Pizzalar" to listOf(
            FoodItem(
                name = "Margherita Pizza",
                imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?q=80&w=400&fit=crop",
                price = "₺120",
                info = "Mozzarella, domates, fesleğen",
                category = "Pizzalar",
                allergens = commonAllergens
            ),
            FoodItem(
                name = "Pepperoni Pizza",
                imageUrl = "https://images.unsplash.com/photo-1534308983496-4fabb1a015ee?q=80&w=400&fit=crop",
                price = "₺140",
                info = "Pepperoni, mozzarella, domates",
                category = "Pizzalar"
            ),
            FoodItem(
                name = "Vejetaryen Pizza",
                imageUrl = "https://images.unsplash.com/photo-1593560708920-61dd98c46a4e?q=80&w=400&fit=crop",
                price = "₺130",
                info = "Mantar, biber, soğan, zeytin",
                category = "Pizzalar"
            )
        ),
        "Burgerler" to listOf(
            FoodItem(
                name = "Cheeseburger",
                imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?q=80&w=400&fit=crop",
                price = "₺95",
                info = "Dana eti, cheddar, turşu",
                category = "Burgerler",
                allergens = commonAllergens
            ),
            FoodItem(
                name = "Tavuk Burger",
                imageUrl = "https://images.unsplash.com/photo-1513185041617-8ab03f83d6c5?q=80&w=400&fit=crop",
                price = "₺85",
                info = "Tavuk göğsü, marul, domates",
                category = "Burgerler"
            ),
            FoodItem(
                name = "Vejetaryen Burger",
                imageUrl = "https://images.unsplash.com/photo-1520072959219-c595dc870360?q=80&w=400&fit=crop",
                price = "₺80",
                info = "Sebze köftesi, avokado, roka",
                category = "Burgerler"
            )
        ),
        "Salatalar" to listOf(
            FoodItem(
                name = "Sezar Salata",
                imageUrl = "https://images.unsplash.com/photo-1550304943-4f24f54ddde9?q=80&w=400&fit=crop",
                price = "₺70",
                info = "Marul, tavuk, kruton, parmesan",
                category = "Salatalar"
            ),
            FoodItem(
                name = "Yunan Salata",
                imageUrl = "https://images.unsplash.com/photo-1644704170910-a0cdf183649b?q=80&w=400&fit=crop",
                price = "₺65",
                info = "Domates, salatalık, zeytin, peynir",
                category = "Salatalar"
            )
        ),
        "İçecekler" to listOf(
            FoodItem(
                name = "Limonata",
                imageUrl = "https://images.unsplash.com/photo-1621263764928-df1444c5e859?q=80&w=400&fit=crop",
                price = "₺35",
                info = "Taze sıkılmış limon suyu, nane",
                category = "İçecekler"
            ),
            FoodItem(
                name = "Çeşitli Sodalar",
                imageUrl = "https://images.unsplash.com/photo-1596803244897-c7dec84a551e?q=80&w=400&fit=crop",
                price = "₺20",
                info = "Cola, Gazoz, Meyve Sodaları",
                category = "İçecekler"
            )
        ),
        "Tatlılar" to listOf(
            FoodItem(
                name = "Tiramisu",
                imageUrl = "https://images.unsplash.com/photo-1551024601-bec78aea704b?q=80&w=400&fit=crop",
                price = "₺60",
                info = "Kahveli, mascarpone, kakao",
                category = "Tatlılar"
            ),
            FoodItem(
                name = "Çikolatalı Sufle",
                imageUrl = "https://images.unsplash.com/photo-1579306194872-64d3b7bac4c2?q=80&w=400&fit=crop",
                price = "₺70",
                info = "Sıcak çikolatalı, dondurma ile",
                category = "Tatlılar"
            )
        ),
        "Yan Ürünler" to listOf(
            FoodItem(
                name = "Patates Kızartması",
                imageUrl = "https://images.unsplash.com/photo-1576107232684-1279f390859f?q=80&w=400&fit=crop",
                price = "₺45",
                info = "Taze patates, özel sos",
                category = "Yan Ürünler"
            ),
            FoodItem(
                name = "Soğan Halkaları",
                imageUrl = "https://images.unsplash.com/photo-1639024471283-03518883512d?q=80&w=400&fit=crop",
                price = "₺50",
                info = "Çıtır soğan halkaları, acı sos",
                category = "Yan Ürünler"
            )
        )
    )

    val categories = listOf("Pizza", "İtalyan", "Fast Food")

    val allItems = mutableListOf<Any>()
    val categoryIndices = mutableMapOf<String, Int>()

    categorizedFoods.forEach { (category, foods) ->
        categoryIndices[category] = allItems.size
        allItems.add(category)
        allItems.addAll(foods)
    }

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val selectedCategory = remember { mutableStateOf(menuCategories.firstOrNull() ?: "") }
    val chipListState = rememberLazyListState()

    LaunchedEffect(selectedCategory.value) {
        val index = menuCategories.indexOf(selectedCategory.value)
        if (index >= 0) {
            chipListState.animateScrollToItem(
                index = index,
                scrollOffset = -chipListState.layoutInfo.viewportSize.width / 2 + 100
            )
        }
    }

    val currentCategoryIndex = remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo

            if (visibleItemsInfo.isEmpty()) return@derivedStateOf 0

            val firstVisibleItem = visibleItemsInfo.first()
            val firstCompletelyVisibleItemIndex =
                if (firstVisibleItem.offset < 0) visibleItemsInfo.getOrNull(1)?.index
                    ?: firstVisibleItem.index
                else firstVisibleItem.index

            var index = firstCompletelyVisibleItemIndex
            while (index >= 0) {
                val item = allItems.getOrNull(index)
                if (item is String) {
                    return@derivedStateOf index
                }
                index--
            }
            0
        }
    }

    LaunchedEffect(currentCategoryIndex.value) {
        val index = currentCategoryIndex.value
        if (index >= 0 && index < allItems.size) {
            val item = allItems[index]
            if (item is String && selectedCategory.value != item) {
                selectedCategory.value = item
            }
        }
    }
    Box() {

        Scaffold(
            bottomBar = {
                AppBottomBar(
                    navController = navController,
                    currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: "",
                )
            },
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors().copy(
                        containerColor = white
                    ),
                    title = {
                        Image(
                            painter = painterResource(Res.drawable.ifsokak_logo),
                            contentDescription = "logo",
                            colorFilter = ColorFilter.tint(Color(0xFFf4244a)),
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.TenantMenu.route)
                            }
                        )
                    },
                    actions = {
                        Box(modifier = Modifier.padding(end = 16.dp)) {
                            IconButton(
                                onClick = {
                                    navController.navigate(Screen.Cart.route)
                                },
                                modifier = Modifier
                                    .padding(1.dp)
                                    .background(darkBlue, CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(Res.drawable.shopping_cart),
                                    contentDescription = "Menu",
                                    colorFilter = ColorFilter.tint(white),
                                    modifier = Modifier.padding(12.dp),
                                )

                            }
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(primary)
                                    .align(Alignment.TopEnd)
                            ) {
                                Text(
                                    text = "2",
                                    color = white,
                                    fontSize = 12.sp,
                                    fontWeight = Bold,
                                    lineHeight = 16.sp,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(2.dp)
                                )
                            }
                        }
                    }
                )

            },
            containerColor = Color.White
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(innerPadding)
                    .background(
                        color = surfaceGray,
                    )
                    .padding(horizontal = 16.dp)
            ) {

                var searchText by remember { mutableStateOf("") }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "IF Sokak",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.Black
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Fast Food, Sokak Lezzetleri, Alkol",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                val scrollState = rememberScrollState()
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(scrollState),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(Res.drawable.clock),
                                contentDescription = "Saat",
                                tint = gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "09:00 - 23:00",
                                color = Color.Black,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(Res.drawable.location),
                                contentDescription = "Uzaklık",
                                tint = gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Çayyolu, Ankara",
                                color = Color.Black,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                HorizontalDivider(color = lightGray)

                Spacer(Modifier.height(8.dp))

                SearchTextField(
                    text = searchText,
                    onValueChange = {
                        searchText = it
                    },
                    placeholder = "Arama yapın...",
                )
                Spacer(modifier = Modifier.size(16.dp))



                Text(
                    text = "Kategoriler",
                    style = MaterialTheme.typography.titleMedium,
                    color = darkPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    state = chipListState,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(menuCategories) { category ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .combinedClickable(
                                    onClick = {
                                        selectedCategory.value = category
                                        categoryIndices[category]?.let { index ->
                                            coroutineScope.launch {
                                                lazyListState.animateScrollToItem(index)
                                            }
                                        }
                                    }
                                )
                        ) {
                            Surface(
                                color = if (selectedCategory.value == category) primary.copy(alpha = 0.2f) else Color(
                                    0xFFF2F2F2
                                ),
                                shape = RoundedCornerShape(10.dp),
                                border = if (selectedCategory.value == category) BorderStroke(
                                    1.dp,
                                    primary
                                ) else null
                            ) {
                                Text(
                                    text = category,
                                    color = if (selectedCategory.value == category) primary else Color.DarkGray,
                                    fontWeight = if (selectedCategory.value == category) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                LazyColumn(
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(allItems.size) { index ->
                        when (val item = allItems[index]) {
                            is String -> {
                                Text(
                                    text = item,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = darkPrimary,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            is FoodItem -> {
                                FoodItemCard(
                                    item = item,
                                    onClick = { navController.navigate(Screen.FoodDetail.route) },
                                    onAddToCartClick = { /* Sepete ekle işlemi */ }
                                )
                            }
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(2f)
                .padding(bottom = 32.dp)
        ) {
            FloatingActionButton(
                onClick = { navController.navigate("qr_scan") },
                containerColor = primary,
                contentColor = white,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.qr),
                    contentDescription = "QR",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryChip(text: String) {
    Surface(
        color = Color(0xFFF2F2F2),
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(
            text = text,
            color = Color.DarkGray,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllergenIcon(allergen: Allergen) {
    AsyncImage(
        model = allergen.iconUrl,
        contentDescription = allergen.name,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
    )
}

val commonAllergens = emptyList<Allergen>()