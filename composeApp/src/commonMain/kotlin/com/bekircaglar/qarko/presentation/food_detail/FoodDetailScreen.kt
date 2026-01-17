package com.bekircaglar.qarko.presentation.food_detail

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.text.style.TextOverflow
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
import com.bekircaglar.qarko.presentation.common.components.AddToCartDialog
import com.bekircaglar.qarko.presentation.common.components.BackButton
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.*
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.ic_heart
import qarko.composeapp.generated.resources.ic_heart_filled
import qarko.composeapp.generated.resources.ic_minus
import qarko.composeapp.generated.resources.ic_plus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun FoodDetailScreen(
    navController: NavController,
    foodItem: FoodItem,
    viewModel: FoodDetailViewModel = koinViewModel()
) {
    var quantity by remember { mutableIntStateOf(1) }
    var showIngredients by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(true) }
    var showAddToCartSuccess by remember { mutableStateOf(false) }

    val selectedSingleOptions = remember { mutableStateMapOf<String, String>() }
    val selectedMultiOptions = remember { mutableStateMapOf<String, Set<String>>() }
    val removedItems = remember { mutableStateMapOf<String, Boolean>() }

    val scope = rememberCoroutineScope()
    val uiState = viewModel.uiState

    LaunchedEffect(foodItem) {
        foodItem.customizationGroups.forEach { group ->
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
        foodItem.removableItems.forEach { item ->
            removedItems[item.id] = item.isRemoved
        }
    }

    val food = foodItem

    val extraPrice by remember {
        derivedStateOf {
            var extra = 0
            food.customizationGroups.forEach { group ->
                when (group.type) {
                    CustomizationType.SINGLE_SELECT -> {
                        val selectedId = selectedSingleOptions[group.id]
                        val option = group.options.find { it.id == selectedId }
                        option?.extraPrice?.let { priceStr ->
                            val price = priceStr.replace("₺", "").replace(",", ".").replace(" ", "").toFloatOrNull() ?: 0f
                            extra += price.toInt()
                        }
                    }
                    CustomizationType.MULTI_SELECT -> {
                        val selectedIds = selectedMultiOptions[group.id] ?: emptySet()
                        group.options.filter { it.id in selectedIds }.forEach { option ->
                            val price = option.extraPrice.replace("₺", "").replace(",", ".").replace(" ", "").toFloatOrNull() ?: 0f
                            extra += price.toInt()
                        }
                    }
                    else -> {}
                }
            }
            extra
        }
    }

    val basePrice = food.price.replace("₺", "").replace(",", ".").replace(" ", "").toFloatOrNull() ?: 0f
    val totalPrice by remember {
        derivedStateOf {
            (basePrice + extraPrice) * quantity
        }
    }

    val isFavorited by remember {
        derivedStateOf {
            FavoritesManager.isFavorite(food.id)
        }
    }
    val favTransition = remember { Animatable(1f) }

    if (showAddToCartSuccess) {
        AddToCartDialog(onDismiss = {
            showAddToCartSuccess = false
            navController.popBackStack()
        })
    }

    Box(modifier = Modifier.fillMaxSize().background(white)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Hero Image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(270.dp)
                ) {
                    AsyncImage(
                        model = food.imageUrl,
                        contentDescription = food.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.8f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.3f))
                        ) {
                            Icon(FeatherIcons.ArrowLeft, null, tint = white, modifier = Modifier.size(24.dp))
                        }

                        IconButton(
                            onClick = {
                                scope.launch {
                                    favTransition.animateTo(1.25f, tween(120))
                                    favTransition.animateTo(1f, tween(120))
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
                                contentDescription = null,
                                tint = if (isFavorited) Color.Red else white,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        if (food.discountPercent > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(topStart = 12.dp))
                                    .background(Color(0xFFD32F2F))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(FeatherIcons.Percent, null, tint = white, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    QText("%${food.discountPercent} İndirim", color = white, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }

                        if (food.rating > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(topStart = if (food.discountPercent > 0) 0.dp else 12.dp))
                                    .background(primary)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(FeatherIcons.Star, null, tint = yellow, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    QText(((food.rating * 10).toInt() / 10.0).toString(), color = white, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Details and Content
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            QText(food.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = darkBlue)
                            Spacer(modifier = Modifier.height(4.dp))
                            QText(food.info, fontSize = 14.sp, color = gray)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            if (!food.originalPrice.isNullOrEmpty()) {
                                QText(food.originalPrice!!, fontSize = 14.sp, color = gray, textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                            }
                            QText(food.price, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TabButtonAnimated("Detaylar", isSelected = showDetails, onClick = { showDetails = true; showIngredients = false })
                        TabButtonAnimated("İçindekiler", isSelected = showIngredients, onClick = { showDetails = false; showIngredients = true })
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = lightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Tab Content
            if (showDetails) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        food.customizationGroups.forEach { group ->
                            CustomDropdownSection(group, selectedSingleOptions, selectedMultiOptions,
                                onSingleSelect = { selectedSingleOptions[group.id] = it },
                                onMultiSelect = { id, selected ->
                                    val current = selectedMultiOptions[group.id]?.toMutableSet() ?: mutableSetOf()
                                    if (selected) current.add(id) else current.remove(id)
                                    selectedMultiOptions[group.id] = current
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        if (food.removableItems.isNotEmpty()) {
                            RemovableItemsSection(food.removableItems, removedItems, onToggleRemove = { removedItems[it] = !(removedItems[it] ?: false) })
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        if (food.allergens.isNotEmpty()) {
                            AllergenSection(food.allergens)
                        }
                    }
                }
            }

            if (showIngredients) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(lightGray.copy(alpha = 0.2f)).padding(16.dp)) {
                        if (food.ingredients.isNotEmpty()) {
                            val sortedIngredients = remember(food.ingredients) { food.ingredients.sortedByDescending { it.isMain } }
                            val rowCount = (sortedIngredients.size + 2) / 3
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                userScrollEnabled = false,
                                modifier = Modifier.height((rowCount * 95).dp).fillMaxWidth()
                            ) {
                                items(sortedIngredients) { IngredientItem(it) }
                            }
                        } else {
                            QText("İçerik bilgisi bulunmuyor", color = gray, fontSize = 14.sp, modifier = Modifier.padding(32.dp).fillMaxWidth(), textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }

        // Fixed Bottom Bar
        Surface(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter), color = white, shadowElevation = 16.dp) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.shadow(1.dp, RoundedCornerShape(12.dp)).clip(RoundedCornerShape(12.dp)).background(lighterGray).padding(4.dp)) {
                    IconButton(enabled = quantity > 1, onClick = { quantity-- }, modifier = Modifier.size(30.dp)) {
                        Icon(painterResource(Res.drawable.ic_minus), null, tint = darkBlue, modifier = Modifier.size(18.dp))
                    }
                    QText(quantity.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = darkPrimary, modifier = Modifier.padding(horizontal = 10.dp))
                    IconButton(onClick = { quantity++ }, modifier = Modifier.size(30.dp)) {
                        Icon(painterResource(Res.drawable.ic_plus), null, tint = darkBlue, modifier = Modifier.size(18.dp))
                    }
                }

                Button(
                    onClick = {
                        CartManager.addToCart(food, quantity, selectedSingleOptions.toMap(), selectedMultiOptions.mapValues { it.value.toSet() }, removedItems.filter { it.value }.keys, totalPrice.toDouble())
                        showAddToCartSuccess = true
                    },
                    modifier = Modifier.height(52.dp).weight(1f).padding(start = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primary)
                ) {
                    QText("Sepete Ekle • ₺${totalPrice.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = white)
                }
            }
        }
    }
}

@Composable
private fun CustomDropdownSection(
    group: CustomizationGroup,
    selectedSingleOptions: Map<String, String>,
    selectedMultiOptions: Map<String, Set<String>>,
    onSingleSelect: (String) -> Unit,
    onMultiSelect: (String, Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val displayText = when (group.type) {
        CustomizationType.SINGLE_SELECT -> {
            val selectedId = selectedSingleOptions[group.id]
            group.options.find { it.id == selectedId }?.name ?: "Seçiniz"
        }
        CustomizationType.MULTI_SELECT -> {
            val selectedCount = selectedMultiOptions[group.id]?.size ?: 0
            if (selectedCount == 0) "Seçiniz" else "$selectedCount Seçim"
        }
        else -> "Seçiniz"
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QText(text = group.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = darkBlue)
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = lighterGray,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (group.isRequired) {
                        Icon(
                            imageVector = FeatherIcons.Check,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(12.dp)
                        )
                        QText(
                            text = "Zorunlu",
                            color = darkGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        QText(
                            text = "İsteğe Bağlı",
                            color = darkGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = lighterGray,
                border = BorderStroke(1.dp, lightGray.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QText(text = displayText, color = darkGray, fontSize = 14.sp)
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = gray,
                        modifier = Modifier.graphicsLayer { rotationZ = if (expanded) 180f else 0f }
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f).background(white)
            ) {
                group.options.forEach { option ->
                    val isSelected = when (group.type) {
                        CustomizationType.SINGLE_SELECT -> selectedSingleOptions[group.id] == option.id
                        CustomizationType.MULTI_SELECT -> selectedMultiOptions[group.id]?.contains(option.id) == true
                        else -> false
                    }

                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (group.type == CustomizationType.MULTI_SELECT) {
                                        Checkbox(checked = isSelected, onCheckedChange = null, colors = CheckboxDefaults.colors(checkedColor = primary))
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    QText(text = option.name, color = if (isSelected) primary else black, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
                                }
                                if (option.extraPrice != "₺0" && option.extraPrice.isNotEmpty()) {
                                    QText(text = "+${option.extraPrice}", color = primary, fontSize = 14.sp)
                                }
                            }
                        },
                        onClick = {
                            if (group.type == CustomizationType.SINGLE_SELECT) {
                                onSingleSelect(option.id)
                                expanded = false
                            } else {
                                onMultiSelect(option.id, !isSelected)
                            }
                        }
                    )
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
            fontSize = 14.sp,
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
                    color = if (isRemoved) Color.Red.copy(alpha = 0.05f) else lighterGray,
                    border = if (isRemoved) BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f)) else BorderStroke(1.dp, lightGray.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onToggleRemove(item.id) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isRemoved) {
                            Icon(androidx.compose.material.icons.Icons.Default.Close, null, tint = Color.Red, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        QText(
                            text = item.name,
                            color = if (isRemoved) Color.Red else darkGray,
                            fontWeight = if (isRemoved) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            textDecoration = if (isRemoved) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
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
            Icon(imageVector = FeatherIcons.AlertTriangle, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            QText(text = "Alerjen Bilgisi", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = darkBlue)
        }

        FlowRow(
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            allergens.forEach { allergen ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFF3E0),
                    border = BorderStroke(1.dp, Color(0xFFFFE0B2))
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

@Composable
private fun IngredientItem(ingredient: Ingredient) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(lighterGray)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (ingredient.isMain) primary.copy(alpha = 0.1f) else white),
            contentAlignment = Alignment.Center
        ) {
            QText(
                text = if (ingredient.iconName.isBlank()) "🌿" else ingredient.iconName,
                fontSize = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        QText(
            text = ingredient.name,
            fontSize = 11.sp,
            fontWeight = if (ingredient.isMain) FontWeight.Bold else FontWeight.Medium,
            color = if (ingredient.isMain) darkBlue else darkGray,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun TabButtonAnimated(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color by animateColorAsState(targetValue = if (isSelected) primary else gray)
    val boxWidth by animateDpAsState(targetValue = if (isSelected) 30.dp else 0.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        QText(
            text = text,
            color = color,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box(modifier = Modifier.height(2.dp).width(boxWidth).background(color, RoundedCornerShape(1.dp)))
    }
}

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0)) }
        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentRowWidth = 0

        placeables.forEach { placeable ->
            if (currentRowWidth + placeable.width + mainAxisSpacing.roundToPx() > constraints.maxWidth && currentRow.isNotEmpty()) {
                rows.add(currentRow)
                currentRow = mutableListOf()
                currentRowWidth = 0
            }
            currentRow.add(placeable)
            currentRowWidth += placeable.width + mainAxisSpacing.roundToPx()
        }
        rows.add(currentRow)

        val height = rows.sumOf { row -> row.maxOf { it.height } } + (rows.size - 1) * crossAxisSpacing.roundToPx()
        layout(constraints.maxWidth, height) {
            var y = 0
            rows.forEach { row ->
                var x = 0
                val rowHeight = row.maxOf { it.height }
                row.forEach { placeable ->
                    placeable.placeRelative(x, y)
                    x += placeable.width + mainAxisSpacing.roundToPx()
                }
                y += rowHeight + crossAxisSpacing.roundToPx()
            }
        }
    }
}
