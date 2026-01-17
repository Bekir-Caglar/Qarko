package com.bekircaglar.qarko.presentation.tenant

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import coil3.compose.AsyncImage
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.data.model.MenuCategory
import com.bekircaglar.qarko.data.manager.CartManager
import com.bekircaglar.qarko.data.manager.TenantSession
import com.bekircaglar.qarko.navigation.AppBottomBar
import com.bekircaglar.qarko.navigation.Cart
import com.bekircaglar.qarko.navigation.FoodDetail
import com.bekircaglar.qarko.navigation.QRScan
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.*
import com.bekircaglar.qarko.presentation.tenant.component.DrawerContent
import com.bekircaglar.qarko.presentation.tenant.component.FoodItemCard
import com.bekircaglar.qarko.util.QarkoTypography
import compose.icons.FeatherIcons
import compose.icons.feathericons.Menu
import compose.icons.feathericons.Search
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import qarko.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TenantMenuScreen(navController: NavController) {

    val viewModel = koinViewModel<TenantMenuViewModel>()
    val uiState = viewModel.uiState
    val currentTenant = TenantSession.currentTenant

    LaunchedEffect(Unit) {
        if (!TenantSession.isSessionActive && uiState.categories.isEmpty()) {
            navController.navigate(QRScan) {
                popUpTo(0)
            }
        }
    }

    val totalCartCount by remember { derivedStateOf { CartManager.totalItemCount } }
    val categories = uiState.categories
    val categorizedItems = uiState.categorizedItems

    val featuredCategory = remember(uiState.allItems) {
        val featuredItems = uiState.allItems.filter { it.isFeatured }
        if (featuredItems.isNotEmpty()) {
            MenuCategory(id = "featured_cat", name = "Öne Çıkanlar", emoji = "⭐")
        } else null
    }

    val menuCategoryNames = remember(categories, categorizedItems, featuredCategory) {
        val list = mutableListOf<String>()
        featuredCategory?.let { list.add(it.name) }
        list.addAll(categories.filter { (categorizedItems[it]?.size ?: 0) > 0 }.map { it.name })
        list
    }

    val allItems = remember(categories, categorizedItems, featuredCategory) {
        val list = mutableListOf<Any>()
        featuredCategory?.let { cat ->
            val items = uiState.allItems.filter { it.isFeatured }
            list.add(cat.name)
            list.addAll(items)
        }
        categories.forEach { category ->
            val items = categorizedItems[category] ?: emptyList()
            if (items.isNotEmpty()) {
                list.add(category.name)
                list.addAll(items)
            }
        }
        list
    }

    val categoryIndices = remember(allItems) {
        val map = mutableMapOf<String, Int>()
        allItems.forEachIndexed { index, item ->
            if (item is String) {
                map[item] = index
            }
        }
        map
    }

    val lazyListState = rememberLazyListState()
    val chipListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    val initialCategory = menuCategoryNames.firstOrNull() ?: ""
    var selectedCategory by remember(initialCategory) { mutableStateOf(initialCategory) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // SCROLL SENKRONİZASYONU - SnapshotFlow ile anlık takip
    LaunchedEffect(lazyListState, allItems) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { firstIndex ->
                // Görünür ilk elemandan geriye doğru en yakın kategori başlığını (String) bul
                var i = firstIndex
                while (i >= 0) {
                    val currentItem = allItems.getOrNull(i)
                    if (currentItem is String) {
                        if (selectedCategory != currentItem) {
                            selectedCategory = currentItem
                        }
                        break
                    }
                    i--
                }
            }
    }

    // Seçili kategori değiştikçe Chip listesini kaydır
    LaunchedEffect(selectedCategory) {
        val index = menuCategoryNames.indexOf(selectedCategory)
        if (index >= 0) {
            chipListState.animateScrollToItem(
                index = index,
                scrollOffset = -150 // Chip'i daha iyi ortalamak için
            )
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = primary)
        }
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onChangeLanguage = { coroutineScope.launch { drawerState.close() } },
                onChangeTheme = { coroutineScope.launch { drawerState.close() } },
                onExitMenu = { 
                    coroutineScope.launch { 
                        drawerState.close()
                        viewModel.clearSession()
                        navController.navigate(QRScan) {
                            popUpTo(0)
                        }
                    } 
                },
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
                                if (currentTenant?.logo != null) {
                                    AsyncImage(
                                        model = currentTenant.logo,
                                        contentDescription = currentTenant.name,
                                        modifier = Modifier.height(40.dp)
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(Res.drawable.qarko_logo),
                                        contentDescription = "logo",
                                        modifier = Modifier.height(40.dp)
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                    Icon(imageVector = FeatherIcons.Menu, contentDescription = "Menü", tint = primary, modifier = Modifier.size(24.dp))
                                }
                            },
                            actions = {
                                Box(modifier = Modifier.padding(end = 16.dp)) {
                                    IconButton(
                                        onClick = { navController.navigate(Cart) },
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
                            items(menuCategoryNames) { category ->
                                val isSelected = selectedCategory == category
                                Card(
                                    onClick = {
                                        selectedCategory = category
                                        categoryIndices[category]?.let { index ->
                                            coroutineScope.launch { 
                                                lazyListState.animateScrollToItem(index) 
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) primary else white
                                    ),
                                    border = if (isSelected) null else BorderStroke(1.dp, lightGray),
                                    elevation = if (isSelected) CardDefaults.cardElevation(defaultElevation = 4.dp) else CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(modifier = Modifier.width(50.dp).height(4.dp).clip(CircleShape).background(primary))
                                }
                            }
                            is FoodItem -> {
                                FoodItemCard(
                                    item = item,
                                    onClick = {
                                        navController.navigate(FoodDetail.fromFoodItem(item))
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
                    onClick = { navController.navigate(QRScan) },
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
