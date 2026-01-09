package com.bekircaglar.qarko.presentation.tenant

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.navigation.AppBottomBar
import com.bekircaglar.qarko.navigation.Screen
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.*
import com.bekircaglar.qarko.presentation.tenant.component.DrawerContent
import com.bekircaglar.qarko.presentation.tenant.component.FoodItemCard
import com.bekircaglar.qarko.util.QarkoTypography
import compose.icons.FeatherIcons
import compose.icons.feathericons.Menu
import compose.icons.feathericons.Search
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TenantMenuScreen(navController: NavController) {
    val menuCategories = listOf(
        "Favoriler", "Başlangıçlar", "Pizzalar", "Burgerler", 
        "Salatalar", "İçecekler", "Tatlılar", "Yan Ürünler"
    )

    // Sepet durumunu takip etmek için state
    val cartItems = remember { mutableStateMapOf<String, Int>() }
    val totalCartCount = cartItems.values.sum()

    val categorizedFoods = remember {
        mapOf(
            "Favoriler" to listOf(
                FoodItem(name = "Margherita Pizza", imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?q=80&w=400&fit=crop", price = "₺120", info = "Mozzarella, domates, fesleğen", category = "Favoriler"),
                FoodItem(name = "Cheeseburger", imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?q=80&w=400&fit=crop", price = "₺95", info = "Dana eti, cheddar, turşu", category = "Favoriler")
            ),
            "Başlangıçlar" to listOf(
                FoodItem(name = "Bruschetta", imageUrl = "https://images.unsplash.com/photo-1506280754576-f6fa8a873550?q=80&w=400&fit=crop", price = "₺75", info = "Domates, sarımsak, fesleğen", category = "Başlangıçlar"),
                FoodItem(name = "Sarımsaklı Ekmek", imageUrl = "https://images.unsplash.com/photo-1573140247632-f8fd74997d5c?q=80&w=400&fit=crop", price = "₺60", info = "Sarımsak, tereyağı, maydanoz", category = "Başlangıçlar")
            ),
            "Pizzalar" to listOf(
                FoodItem(name = "Margherita Pizza", imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?q=80&w=400&fit=crop", price = "₺120", info = "Mozzarella, domates, fesleğen", category = "Pizzalar"),
                FoodItem(name = "Pepperoni Pizza", imageUrl = "https://images.unsplash.com/photo-1534308983496-4fabb1a015ee?q=80&w=400&fit=crop", price = "₺140", info = "Pepperoni, mozzarella, domates", category = "Pizzalar"),
                FoodItem(name = "Vejetaryen Pizza", imageUrl = "https://images.unsplash.com/photo-1593560708920-61dd98c46a4e?q=80&w=400&fit=crop", price = "₺130", info = "Mantar, biber, soğan, zeytin", category = "Pizzalar")
            ),
            "Burgerler" to listOf(
                FoodItem(name = "Cheeseburger", imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?q=80&w=400&fit=crop", price = "₺95", info = "Dana eti, cheddar, turşu", category = "Burgerler"),
                FoodItem(name = "Tavuk Burger", imageUrl = "https://images.unsplash.com/photo-1513185041617-8ab03f83d6c5?q=80&w=400&fit=crop", price = "₺85", info = "Tavuk göğsü, marul, domates", category = "Burgerler"),
                FoodItem(name = "Vejetaryen Burger", imageUrl = "https://images.unsplash.com/photo-1520072959219-c595dc870360?q=80&w=400&fit=crop", price = "₺80", info = "Sebze köftesi, avokado, roka", category = "Burgerler")
            ),
            "Salatalar" to listOf(
                FoodItem(name = "Sezar Salata", imageUrl = "https://images.unsplash.com/photo-1550304943-4f24f54ddde9?q=80&w=400&fit=crop", price = "₺70", info = "Marul, tavuk, kruton, parmesan", category = "Salatalar"),
                FoodItem(name = "Yunan Salata", imageUrl = "https://images.unsplash.com/photo-1644704170910-a0cdf183649b?q=80&w=400&fit=crop", price = "₺65", info = "Domates, salatalık, zeytin, peynir", category = "Salatalar")
            ),
            "İçecekler" to listOf(
                FoodItem(name = "Limonata", imageUrl = "https://images.unsplash.com/photo-1621263764928-df1444c5e859?q=80&w=400&fit=crop", price = "₺35", info = "Taze sıkılmış limon suyu, nane", category = "İçecekler"),
                FoodItem(name = "Çeşitli Sodalar", imageUrl = "https://images.unsplash.com/photo-1596803244897-c7dec84a551e?q=80&w=400&fit=crop", price = "₺20", info = "Cola, Gazoz, Meyve Sodaları", category = "İçecekler")
            ),
            "Tatlılar" to listOf(
                FoodItem(name = "Tiramisu", imageUrl = "https://images.unsplash.com/photo-1551024601-bec78aea704b?q=80&w=400&fit=crop", price = "₺60", info = "Kahveli, maccarone, kakao", category = "Tatlılar"),
                FoodItem(name = "Çikolatalı Sufle", imageUrl = "https://images.unsplash.com/photo-1579306194872-64d3b7bac4c2?q=80&w=400&fit=crop", price = "₺70", info = "Sıcak çikolatalı, dondurma ile", category = "Tatlılar")
            ),
            "Yan Ürünler" to listOf(
                FoodItem(name = "Patates Kızartması", imageUrl = "https://images.unsplash.com/photo-1576107232684-1279f390859f?q=80&w=400&fit=crop", price = "₺45", info = "Taze patates, özel sos", category = "Yan Ürünler"),
                FoodItem(name = "Soğan Halkaları", imageUrl = "https://images.unsplash.com/photo-1639024471283-03518883512d?q=80&w=400&fit=crop", price = "₺50", info = "Çıtır soğan halkaları, acı sos", category = "Yan Ürünler")
            )
        )
    }

    val allItems = remember {
        val list = mutableListOf<Any>()
        categorizedFoods.forEach { (category, foods) ->
            list.add(category)
            list.addAll(foods)
        }
        list
    }

    val categoryIndices = remember {
        val map = mutableMapOf<String, Int>()
        var currentIndex = 0
        categorizedFoods.forEach { (category, foods) ->
            map[category] = currentIndex
            currentIndex += foods.size + 1
        }
        map
    }

    val lazyListState = rememberLazyListState()
    val chipListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val selectedCategory = remember { mutableStateOf(menuCategories.first()) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val currentCategoryIndexByScroll = remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) return@derivedStateOf 0

            val isAtBottom = visibleItemsInfo.last().index == layoutInfo.totalItemsCount - 1
            if (isAtBottom && visibleItemsInfo.last().offset <= layoutInfo.viewportEndOffset) {
                var searchIndex = allItems.size - 1
                while (searchIndex >= 0) {
                    if (allItems[searchIndex] is String) return@derivedStateOf searchIndex
                    searchIndex--
                }
            }

            val firstVisibleIndex = visibleItemsInfo.first().index
            var searchIndex = firstVisibleIndex
            while (searchIndex >= 0) {
                if (allItems.getOrNull(searchIndex) is String) return@derivedStateOf searchIndex
                searchIndex--
            }
            0
        }
    }

    LaunchedEffect(currentCategoryIndexByScroll.value) {
        val item = allItems[currentCategoryIndexByScroll.value]
        if (item is String) selectedCategory.value = item
    }

    LaunchedEffect(selectedCategory.value) {
        val index = menuCategories.indexOf(selectedCategory.value)
        if (index >= 0) {
            val layoutInfo = chipListState.layoutInfo
            val viewportWidth = layoutInfo.viewportSize.width
            if (viewportWidth > 0) {
                chipListState.animateScrollToItem(
                    index = index,
                    scrollOffset = -(viewportWidth / 2) + 100
                )
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onChangeLanguage = { coroutineScope.launch { drawerState.close() } },
                onChangeTheme = { coroutineScope.launch { drawerState.close() } },
                onExitMenu = { coroutineScope.launch { drawerState.close() } },
                onLogout = { coroutineScope.launch { drawerState.close() } }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    Column(modifier = Modifier.background(white)) {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = white),
                            title = {
                                Image(
                                    painter = painterResource(Res.drawable.ifsokak_logo),
                                    contentDescription = "logo",
                                    colorFilter = ColorFilter.tint(Color(0xFFf4244a)),
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                    Icon(imageVector = FeatherIcons.Menu, contentDescription = "Menü", tint = primary, modifier = Modifier.size(24.dp))
                                }
                            },
                            actions = {
                                Box(modifier = Modifier.padding(end = 16.dp)) {
                                    IconButton(
                                        onClick = { navController.navigate(Screen.Cart.route) },
                                        modifier = Modifier.padding(1.dp).background(darkBlue, CircleShape)
                                    ) {
                                        Image(
                                            painter = painterResource(Res.drawable.ic_basket),
                                            contentDescription = "Menu",
                                            colorFilter = ColorFilter.tint(white),
                                            modifier = Modifier.padding(12.dp),
                                        )
                                    }
                                    
                                    this@TopAppBar.AnimatedVisibility(
                                        visible = totalCartCount > 0,
                                        enter = scaleIn() + fadeIn(),
                                        exit = scaleOut() + fadeOut(),
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    ) {
                                        Box(
                                            modifier = Modifier.size(24.dp).clip(CircleShape).background(primary)
                                        ) {
                                            QText(
                                                text = totalCartCount.toString(),
                                                color = white,
                                                fontSize = 12.sp,
                                                fontWeight = Bold,
                                                lineHeight = 16.sp,
                                                modifier = Modifier.align(Alignment.Center).padding(2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        )
                        
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).height(52.dp),
                            placeholder = { QText("Sevdiğin yemeği ara...", color = gray, fontSize = 14.sp) },
                            leadingIcon = { Icon(imageVector = FeatherIcons.Search, contentDescription = null, tint = gray, modifier = Modifier.size(20.dp)) },
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = lightGray.copy(alpha = 0.3f),
                                unfocusedContainerColor = lightGray.copy(alpha = 0.3f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )

                        LazyRow(
                            state = chipListState,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(menuCategories) { category ->
                                val isSelected = selectedCategory.value == category
                                Surface(
                                    modifier = Modifier.clickable {
                                        selectedCategory.value = category
                                        categoryIndices[category]?.let { index ->
                                            coroutineScope.launch { lazyListState.animateScrollToItem(index) }
                                        }
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) primary else white,
                                    border = if (isSelected) null else BorderStroke(1.dp, lightGray),
                                    shadowElevation = if (isSelected) 4.dp else 0.dp
                                ) {
                                    QText(
                                        text = category,
                                        color = if (isSelected) white else gray,
                                        fontWeight = if (isSelected) Bold else FontWeight.Medium,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }
                        HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = lightGray)
                    }
                },
                bottomBar = {
                    AppBottomBar(
                        navController = navController,
                        currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: ""
                    )
                },
                containerColor = surfaceGray
            ) { innerPadding ->
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(allItems.size) { index ->
                        when (val item = allItems[index]) {
                            is String -> {
                                Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
                                    QText(text = item, textStyle = QarkoTypography.headlineSmall, color = darkBlue, fontWeight = FontWeight.ExtraBold)
                                    Box(modifier = Modifier.width(40.dp).height(4.dp).clip(CircleShape).background(primary))
                                }
                            }
                            is FoodItem -> {
                                FoodItemCard(
                                    item = item,
                                    quantity = cartItems[item.name] ?: 0,
                                    onClick = { navController.navigate(Screen.FoodDetail.route) },
                                    onAddToCartClick = { 
                                        val current = cartItems[item.name] ?: 0
                                        cartItems[item.name] = current + 1
                                    },
                                    onRemoveFromCartClick = {
                                        val current = cartItems[item.name] ?: 0
                                        if (current > 1) {
                                            cartItems[item.name] = current - 1
                                        } else {
                                            cartItems.remove(item.name)
                                        }
                                    }
                                )
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(200.dp)) }
                }
            }

            Box(modifier = Modifier.align(Alignment.BottomCenter).zIndex(2f).padding(bottom = 24.dp)) {
                FloatingActionButton(
                    onClick = { navController.navigate("qr_scan") },
                    containerColor = primary,
                    contentColor = white,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(painter = painterResource(Res.drawable.ic_qr), contentDescription = "QR", modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}
