package com.bekircaglar.qarko.presentation.food_detail

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.bekircaglar.qarko.*
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.data.model.IngredientWithIcon
import com.bekircaglar.qarko.presentation.common.components.BackButton
import kotlinx.coroutines.launch
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.add
import qarko.composeapp.generated.resources.favourite
import qarko.composeapp.generated.resources.minus
import qarko.composeapp.generated.resources.star
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun FoodDetailScreen(navController: NavController) {
    var quantity by remember { mutableIntStateOf(1) }
    var showIngredients by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(true) }
    val selectedOptions = remember { mutableStateMapOf<String, String>() }

    val scope = rememberCoroutineScope()

    val food = FoodItem(
        name = "Pizza",
        imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591",
        info = "Gerçek İtalyan pizzası, taze malzemelerle hazırlanmış.",
        price = "$12.99"
    )

    val backgroundTint = when {
        food.name.contains("Pizza", ignoreCase = true) -> Color(0xFFFFF3E0)
        food.name.contains("Burger", ignoreCase = true) -> Color(0xFFFFECB3)
        food.name.contains("Salata", ignoreCase = true) -> Color(0xFFE8F5E9)
        else -> Color(0xFFF5F5F5)
    }

    val options = when {
        food.name.contains("Pizza", ignoreCase = true) -> mapOf(
            "Boy" to listOf("Küçük", "Orta", "Büyük"),
            "Hamur" to listOf("İnce", "Normal", "Kalın")
        )

        food.name.contains("Burger", ignoreCase = true) -> mapOf(
            "Et Pişirme" to listOf("Az", "Orta", "Çok"),
            "Ekstra" to listOf("Peynir", "Bacon", "Soğan")
        )

        else -> emptyMap()
    }

    val ingredients = listOf(
        IngredientWithIcon("Domates", Icons.Default.FavoriteBorder),
        IngredientWithIcon("Peynir", Icons.Default.FavoriteBorder),
        IngredientWithIcon("Zeytinyağı", Icons.Default.FavoriteBorder),
        IngredientWithIcon("Tuz", Icons.Default.FavoriteBorder),
        IngredientWithIcon("Karabiber", Icons.Default.FavoriteBorder)
    )

    // Animation for entry (screen-wide)
    val enterTransition = rememberInfiniteTransition(label = "screen transition")
    val bgAlpha by enterTransition.animateFloat(
        initialValue = 0.85f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bgAlpha"
    )

    Scaffold(
        modifier = Modifier,
        topBar = {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = fadeOut()
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Yemek Detayı",
                            color = darkGray,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors().copy(
                        containerColor = white
                    ),
                    navigationIcon = {
                        BackButton(
                            iconColor = black,
                            backgroundColor = lightGray.copy(0.6f),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            navController.popBackStack()
                        }
                    },
                    actions = {
                        // Button animated with scale
                        val favTransition = remember { Animatable(1f) }
                        IconButton(
                            onClick = {
                                // Favori ikonunu animasyonla büyüt/küçült
                                scope.launch {
                                    favTransition.animateTo(
                                        targetValue = 1.25f,
                                        animationSpec = tween(120)
                                    )
                                    favTransition.animateTo(
                                        targetValue = 1f,
                                        animationSpec = tween(120)
                                    )
                                }
                            },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .graphicsLayer {
                                    scaleX = favTransition.value
                                    scaleY = favTransition.value
                                }
                                .background(
                                    color = lightGray.copy(0.6f),
                                    shape = CircleShape
                                )
                                .size(36.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.favourite),
                                contentDescription = "Favori",
                                tint = black,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                )
            }
        },
        containerColor = white.copy(alpha = bgAlpha)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Food Image with animated shadow and scale
                var imageLoaded by remember { mutableStateOf(false) }
                val imageScale by animateFloatAsState(
                    targetValue = if (imageLoaded) 1f else 0.9f,
                    animationSpec = tween(800, easing = FastOutSlowInEasing), label = "imageScale"
                )
                val imageAlpha by animateFloatAsState(
                    targetValue = if (imageLoaded) 1f else 0.6f,
                    animationSpec = tween(800, easing = FastOutSlowInEasing), label = "imageAlpha"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    AsyncImage(
                        model = food.imageUrl,
                        contentDescription = food.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .graphicsLayer {
                                scaleX = imageScale
                                scaleY = imageScale
                                alpha = imageAlpha
                                shadowElevation = if (imageLoaded) 18f else 2f
                            },
                        onSuccess = { imageLoaded = true }
                    )

                    androidx.compose.animation.AnimatedVisibility(
                        visible = true,
                        enter = slideInHorizontally(initialOffsetX = { it / 2 }) + fadeIn(),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(primary.copy(0.6f))
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
                                Text(
                                    text = "4.8",
                                    color = white,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                AnimatedContent(
                    targetState = food,
                    transitionSpec = {
                        slideInHorizontally(animationSpec = tween(600)) + fadeIn() with
                                slideOutHorizontally(animationSpec = tween(600)) + fadeOut()
                    },
                    label = "FoodDetails"
                ) { _ ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
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

                                Text(
                                    text = food.name,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = darkBlue
                                )

                                Text(
                                    text = food.info,
                                    fontSize = 14.sp,
                                    color = gray
                                )

                            }

                            Text(
                                text = food.price,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = primary,
                                modifier = Modifier
                                    .padding(start = 16.dp)
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
                        Divider(color = lightGray)
                        Spacer(modifier = Modifier.height(16.dp))

                        AnimatedVisibility(
                            visible = showDetails,
                            enter = fadeIn(animationSpec = tween(300)) + expandVertically(),
                            exit = fadeOut(animationSpec = tween(300)) + shrinkVertically()
                        ) {
                            Column {
                                Text(
                                    text = "${food.name}, ${food.info}. En kaliteli malzemelerle hazırlanır ve özenle servis edilir.",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 22.sp
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                if (options.isNotEmpty()) {
                                    options.forEach { (optionType, values) ->
                                        AnimatedVisibility(
                                            true,
                                            enter = fadeIn() + slideInHorizontally(),
                                            exit = fadeOut()
                                        ) {
                                            Text(
                                                text = optionType,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = darkBlue
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            values.forEach { value ->
                                                val isSelected =
                                                    selectedOptions[optionType] == value
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = if (isSelected) primary.copy(alpha = 0.2f) else lightGray.copy(
                                                        alpha = 0.5f
                                                    ),
                                                    border = if (isSelected) BorderStroke(
                                                        1.dp,
                                                        primary
                                                    ) else null,
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .clickable {
                                                            selectedOptions[optionType] = value
                                                        }
                                                        .graphicsLayer {
                                                            scaleX = if (isSelected) 1.1f else 1f
                                                            scaleY = if (isSelected) 1.1f else 1f
                                                        }
                                                ) {
                                                    Text(
                                                        text = value,
                                                        color = if (isSelected) primary else darkGray,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                        modifier = Modifier.padding(
                                                            horizontal = 16.dp,
                                                            vertical = 8.dp
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = showIngredients,
                            enter = fadeIn(animationSpec = tween(300)) + expandVertically(),
                            exit = fadeOut(animationSpec = tween(300)) + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(backgroundTint.copy(alpha = 0.3f))
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "İçindekiler",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = darkBlue,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.height(180.dp)
                                ) {
                                    items(ingredients) { ingredient ->
                                        IngredientItemWithIconAnimated(ingredient)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }

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
                    // Animated quantity selector
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
                            .background(Color(0xFFF5F5F5))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        IconButton(
                            enabled = quantity > 1,
                            onClick = { quantity-- },
                            modifier = Modifier
                                .size(36.dp),
                            colors = IconButtonDefaults.iconButtonColors().copy(
                                contentColor = darkBlue
                            )
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.minus),
                                contentDescription = "Azalt",
                                modifier = Modifier.padding(8.dp)
                            )
                        }


                        Text(
                            text = quantity.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )


                        IconButton(
                            onClick = { quantity++ },
                            modifier = Modifier
                                .size(36.dp),
                            colors = IconButtonDefaults.iconButtonColors().copy(
                                contentColor = darkBlue
                            )
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.add),
                                contentDescription = "Artır",
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                    }


                    Button(
                        onClick = {
                            pressed = true
                            // short feedback
                            scope.launch {
                                pressed = true
                                kotlinx.coroutines.delay(140)
                                pressed = false
                            }
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .width(200.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primary)
                    ) {
                        Text(
                            text = "Sepete Ekle",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// ANIMATED COMPONENTS BELOW

@Composable
fun IngredientItemWithIconAnimated(ingredient: IngredientWithIcon) {
    // Entry animation for ingredient items
    val transition = rememberInfiniteTransition(label = "ingredientPulse")
    val pulse by transition.animateFloat(
        initialValue = 1f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ingredientPulseValue"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(white)
            .padding(8.dp)
            .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
            }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ingredient.icon,
                contentDescription = ingredient.name,
                tint = primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = ingredient.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = darkGray,
            textAlign = TextAlign.Center
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
        Text(
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
                    RoundedCornerShape(1.dp)
                )
        )
    }
}