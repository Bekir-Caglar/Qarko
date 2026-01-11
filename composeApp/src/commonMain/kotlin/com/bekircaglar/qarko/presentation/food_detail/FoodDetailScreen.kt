package com.bekircaglar.qarko.presentation.food_detail

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.bekircaglar.qarko.data.model.Allergen
import com.bekircaglar.qarko.data.model.CustomizationGroup
import com.bekircaglar.qarko.data.model.CustomizationType
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.data.model.Ingredient
import com.bekircaglar.qarko.data.model.RemovableItem
import com.bekircaglar.qarko.data.manager.CartManager
import com.bekircaglar.qarko.data.manager.FavoritesManager
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.darkBlue
import com.bekircaglar.qarko.presentation.common.theme.darkGray
import com.bekircaglar.qarko.presentation.common.theme.darkPrimary
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.presentation.common.theme.lightGray
import com.bekircaglar.qarko.presentation.common.theme.lighterGray
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.presentation.common.theme.white
import com.bekircaglar.qarko.presentation.common.theme.yellow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.ic_heart
import qarko.composeapp.generated.resources.ic_heart_filled
import qarko.composeapp.generated.resources.ic_minus
import qarko.composeapp.generated.resources.ic_plus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun FoodDetailScreen(navController: NavController, foodItem: FoodItem) {
    var quantity by remember { mutableIntStateOf(1) }
    var showIngredients by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(true) }

    // Dinamik seçimler için state'ler
    val selectedSingleOptions = remember { mutableStateMapOf<String, String>() }
    val selectedMultiOptions = remember { mutableStateMapOf<String, Set<String>>() }
    val removedItems = remember { mutableStateMapOf<String, Boolean>() }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val food = foodItem

    // Default değerleri ayarla
    LaunchedEffect(food) {
        food.customizationGroups.forEach { group ->
            when (group.type) {
                CustomizationType.SINGLE_SELECT -> {
                    val defaultOption = group.options.find { it.isDefault }?.id ?: group.options.firstOrNull()?.id
                    defaultOption?.let { selectedSingleOptions[group.id] = it }
                }
                CustomizationType.MULTI_SELECT -> {
                    val defaultOptions = group.options.filter { it.isDefault }.map { it.id }.toSet()
                    selectedMultiOptions[group.id] = defaultOptions
                }
                else -> {}
            }
        }
        food.removableItems.forEach { item ->
            removedItems[item.id] = item.isRemoved
        }
    }

    // Ekstra fiyat hesaplama - derivedStateOf kullanarak reaktif hale getiriyoruz
    val extraPrice by remember {
        derivedStateOf {
            var extra = 0
            food.customizationGroups.forEach { group ->
                when (group.type) {
                    CustomizationType.SINGLE_SELECT -> {
                        val selectedId = selectedSingleOptions[group.id]
                        val option = group.options.find { it.id == selectedId }
                        option?.extraPrice?.let { priceStr ->
                            val price = priceStr.replace("₺", "").replace(",", ".").toFloatOrNull() ?: 0f
                            extra += price.toInt()
                        }
                    }
                    CustomizationType.MULTI_SELECT -> {
                        val selectedIds = selectedMultiOptions[group.id] ?: emptySet()
                        group.options.filter { it.id in selectedIds }.forEach { option ->
                            val price = option.extraPrice.replace("₺", "").replace(",", ".").toFloatOrNull() ?: 0f
                            extra += price.toInt()
                        }
                    }
                    else -> {}
                }
            }
            extra
        }
    }

    val basePrice = food.price.replace("₺", "").replace(",", ".").toFloatOrNull() ?: 0f
    val totalPrice by remember {
        derivedStateOf {
            (basePrice + extraPrice) * quantity
        }
    }

    // Favori state - FavoritesManager'dan al
    val isFavorited by remember {
        derivedStateOf {
            FavoritesManager.isFavorite(food.id)
        }
    }
    val favTransition = remember { Animatable(1f) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Full-width Hero Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp)
            ) {
                // Background Image - Full width, no padding
                AsyncImage(
                    model = food.imageUrl,
                    contentDescription = food.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay from top (dark to transparent)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.9f),
                                    Color.Black.copy(alpha = 0.7f),
                                    Color.Black.copy(alpha = 0.7f),
                                    Color.Black.copy(alpha = 0.5f),
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Top Bar with back button, title and favorite - on top of image
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Geri",
                            tint = white,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Title
                    QText(
                        text = "Ürün Detayı",
                        color = white,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Favorite Button
                    IconButton(
                        onClick = {
                            scope.launch {
                                favTransition.animateTo(
                                    targetValue = 1.25f,
                                    animationSpec = tween(120)
                                )
                                favTransition.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(120)
                                )
                                FavoritesManager.toggleFavorite(food)
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.3f))
                            .graphicsLayer {
                                scaleX = favTransition.value
                                scaleY = favTransition.value
                            }
                    ) {
                        Icon(
                            painter = if (isFavorited) painterResource(Res.drawable.ic_heart_filled) else painterResource(Res.drawable.ic_heart),
                            contentDescription = "Favori",
                            tint = if (isFavorited) Color.Red else white,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Rating badge at bottom right of image
                if (food.rating > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .clip(RoundedCornerShape(topStart = 12.dp))
                            .background(primary)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Puan",
                                tint = yellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            QText(
                                text = ((food.rating * 10).toInt() / 10.0).toString(),
                                color = white,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (food.ratingCount > 0) {
                                QText(
                                    text = " (${food.ratingCount})",
                                    color = white.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Food Details Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(white)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        QText(
                            text = food.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkBlue
                        )

                        QText(
                            text = food.info,
                            fontSize = 14.sp,
                            color = gray
                        )
                    }

                    QText(
                        text = food.price,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = primary,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TabButtonAnimated(
                        text = "Detaylar",
                        isSelected = showDetails,
                        onClick = {
                            showDetails = true
                            showIngredients = false
                        }
                    )

                    TabButtonAnimated(
                        text = "İçindekiler",
                        isSelected = showIngredients,
                        onClick = {
                            showDetails = false
                            showIngredients = true
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = lightGray)
                Spacer(modifier = Modifier.height(16.dp))

                if (showDetails) {
                    Column {
                        QText(
                            text = "${food.name}, ${food.info}. En kaliteli malzemelerle hazırlanır ve özenle servis edilir.",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Dinamik özelleştirme grupları
                        food.customizationGroups.forEach { group ->
                            CustomizationGroupSection(
                                group = group,
                                selectedSingleOptions = selectedSingleOptions,
                                selectedMultiOptions = selectedMultiOptions,
                                onSingleSelect = { optionId ->
                                    selectedSingleOptions[group.id] = optionId
                                },
                                onMultiSelect = { optionId, isSelected ->
                                    // Yeni bir set oluşturarak state'i güncelle (recomposition için)
                                    val currentSet = selectedMultiOptions[group.id]?.toMutableSet() ?: mutableSetOf()
                                    if (isSelected) {
                                        currentSet.add(optionId)
                                    } else {
                                        currentSet.remove(optionId)
                                    }
                                    // Yeni set atayarak state değişikliğini tetikle
                                    selectedMultiOptions[group.id] = currentSet
                                }
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        // Çıkarılabilir malzemeler
                        if (food.removableItems.isNotEmpty()) {
                            RemovableItemsSection(
                                removableItems = food.removableItems,
                                removedItems = removedItems,
                                onToggleRemove = { itemId ->
                                    removedItems[itemId] = !(removedItems[itemId] ?: false)
                                }
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        // Alerjen bilgileri
                        if (food.allergens.isNotEmpty()) {
                            AllergenSection(allergens = food.allergens)
                        }
                    }
                }

                if (showIngredients) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(lightGray.copy(alpha = 0.2f))
                            .padding(16.dp)
                    ) {
                        if (food.ingredients.isNotEmpty()) {
                            QText(
                                text = "İçindekiler",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = darkBlue,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.height(
                                    ((food.ingredients.size / 3 + 1) * 90).coerceAtMost(270).dp
                                )
                            ) {
                                items(food.ingredients) { ingredient ->
                                    IngredientItem(ingredient = ingredient)
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                QText(
                                    text = "İçerik bilgisi bulunmuyor",
                                    color = gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // Bottom spacing for the fixed bottom bar
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Fixed Bottom Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = white,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity selector
                var pressed by remember { mutableStateOf(false) }
                val scale by animateFloatAsState(
                    if (pressed) 0.93f else 1f,
                    label = "buttonScale"
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .shadow(2.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(lighterGray)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    IconButton(
                        enabled = quantity > 1,
                        onClick = { quantity-- },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.iconButtonColors().copy(
                            contentColor = darkBlue
                        )
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_minus),
                            contentDescription = "Azalt",
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    QText(
                        text = quantity.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    IconButton(
                        onClick = { quantity++ },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.iconButtonColors().copy(
                            contentColor = darkBlue
                        )
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_plus),
                            contentDescription = "Artır",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        pressed = true
                        scope.launch {
                            pressed = true
                            kotlinx.coroutines.delay(140)
                            pressed = false

                            // Add to cart using CartManager
                            CartManager.addToCart(
                                foodItem = food,
                                quantity = quantity,
                                selectedSingleOptions = selectedSingleOptions.toMap(),
                                selectedMultiOptions = selectedMultiOptions.mapValues { it.value.toSet() },
                                removedItems = removedItems.filter { it.value }.keys,
                                totalPrice = totalPrice.toDouble()
                            )

                            // Navigate back after adding to cart
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primary)
                ) {
                    QText(
                        text = "Sepete Ekle • ₺${totalPrice.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

// DINAMIK COMPONENT'LER

@Composable
private fun CustomizationGroupSection(
    group: CustomizationGroup,
    selectedSingleOptions: Map<String, String>,
    selectedMultiOptions: Map<String, Set<String>>,
    onSingleSelect: (String) -> Unit,
    onMultiSelect: (String, Boolean) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            QText(
                text = group.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = darkBlue
            )
            if (group.isRequired) {
                Spacer(modifier = Modifier.width(4.dp))
                QText(
                    text = "*",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(group.options) { option ->
                val isSelected = when (group.type) {
                    CustomizationType.SINGLE_SELECT -> selectedSingleOptions[group.id] == option.id
                    CustomizationType.MULTI_SELECT -> selectedMultiOptions[group.id]?.contains(option.id) == true
                    else -> false
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) primary.copy(alpha = 0.15f) else lightGray.copy(alpha = 0.4f),
                    border = if (isSelected) BorderStroke(2.dp, primary) else BorderStroke(1.dp, lightGray),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            when (group.type) {
                                CustomizationType.SINGLE_SELECT -> onSingleSelect(option.id)
                                CustomizationType.MULTI_SELECT -> onMultiSelect(option.id, !isSelected)
                                else -> {}
                            }
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            // Sabit bir yükseklik vererek tüm kutuların aynı boyutta olmasını sağlıyoruz
                            // 48.dp veya 50.dp içeriğinize göre idealdir
                            .height(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center // İçeriği dikeyde ortalar
                    ) {
                        QText(
                            text = option.name,
                            color = if (isSelected) primary else darkGray,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        // Fiyat bilgisi varsa göster, yoksa gizli ama yer kaplayan (Spacer gibi) bir yapı kur
                        if (option.extraPrice != "₺0" && option.extraPrice.isNotEmpty()) {
                            QText(
                                text = "+${option.extraPrice}",
                                color = if (isSelected) primary else gray,
                                fontSize = 12.sp
                            )
                        }
                        // else bloğunu tamamen siliyoruz çünkü Arrangement.Center
                        // sayesinde fiyat yoksa isim zaten tam ortaya yerleşecek.
                    }

                }
            }
        }
    }
}

@Composable
private fun RemovableItemsSection(
    removableItems: List<RemovableItem>,
    removedItems: Map<String, Boolean>,
    onToggleRemove: (String) -> Unit
) {
    Column {
        QText(
            text = "Çıkarmak İstedikleriniz",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = darkBlue,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(removableItems) { item ->
                val isRemoved = removedItems[item.id] == true

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isRemoved) Color.Red.copy(alpha = 0.1f) else lightGray.copy(alpha = 0.4f),
                    border = if (isRemoved) BorderStroke(2.dp, Color.Red) else BorderStroke(1.dp, lightGray),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onToggleRemove(item.id) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isRemoved) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        QText(
                            text = if (isRemoved) "̶${item.name}̶" else item.name,
                            color = if (isRemoved) Color.Red else darkGray,
                            fontWeight = if (isRemoved) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AllergenSection(allergens: List<Allergen>) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFFF9800),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            QText(
                text = "Alerjen Bilgisi",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = darkBlue
            )
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFF3E0),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                allergens.forEach { allergen ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFE0B2)
                    ) {
                        QText(
                            text = allergen.displayName,
                            color = Color(0xFFE65100),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IngredientItem(ingredient: Ingredient) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(white)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (ingredient.isMain) primary.copy(alpha = 0.2f) else lightGray.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            // Show emoji icon instead of star/check icons
            QText(
                text = ingredient.iconName,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        QText(
            text = ingredient.name,
            fontSize = 11.sp,
            fontWeight = if (ingredient.isMain) FontWeight.Bold else FontWeight.Medium,
            color = if (ingredient.isMain) darkBlue else darkGray,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
fun TabButtonAnimated(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Animate color and size for selected tab
    val color by animateColorAsState(
        targetValue = if (isSelected) primary else gray,
        animationSpec = tween(400), label = "TabColor"
    )
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal

    val boxWidth by animateDpAsState(
        targetValue = if (isSelected) 40.dp else 18.dp,
        animationSpec = tween(400), label = "TabUnderline"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .graphicsLayer {
                scaleX = if (isSelected) 1.08f else 1f
                scaleY = if (isSelected) 1.08f else 1f
            }
    ) {
        QText(
            text = text,
            color = color,
            fontWeight = fontWeight,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Box(
            modifier = Modifier
                .height(2.dp)
                .width(boxWidth)
                .background(
                    color = color,
                    shape = RoundedCornerShape(1.dp)
                )
        )
    }
}