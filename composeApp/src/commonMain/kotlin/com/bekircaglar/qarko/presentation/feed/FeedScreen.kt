package com.bekircaglar.qarko.presentation.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bekircaglar.qarko.black
import com.bekircaglar.qarko.darkBlue
import com.bekircaglar.qarko.data.model.FoodCategory
import com.bekircaglar.qarko.data.model.Restaurant
import com.bekircaglar.qarko.gray
import com.bekircaglar.qarko.navigation.Screen
import com.bekircaglar.qarko.presentation.feed.component.CategoryItem
import com.bekircaglar.qarko.presentation.feed.component.RestaurantCard
import com.bekircaglar.qarko.presentation.feed.component.SearchTextField
import com.bekircaglar.qarko.primary
import com.bekircaglar.qarko.white
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.arrow_right
import qarko.composeapp.generated.resources.compose_multiplatform
import qarko.composeapp.generated.resources.menu_left
import qarko.composeapp.generated.resources.shopping_cart


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(navController: NavController) {
    val categories = listOf(
        FoodCategory(
            "Pizza",
            "https://images.unsplash.com/photo-1513104890138-7c749659a591?q=80&w=300&fit=crop",
            "₺45"
        ),
        FoodCategory(
            "Burger",
            "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?q=80&w=300&fit=crop",
            "₺60"
        ),
        FoodCategory(
            "Sushi",
            "https://images.unsplash.com/photo-1583623025817-d180a2221d0a?q=80&w=300&fit=crop",
            "₺75"
        ),
        FoodCategory(
            "Pasta",
            "https://images.unsplash.com/photo-1556761223-4c4282c73f77?q=80&w=300&fit=crop",
            "₺55"
        ),
        FoodCategory(
            "Dessert",
            "https://images.unsplash.com/photo-1551024601-bec78aea704b?q=80&w=300&fit=crop",
            "₺35"
        ),
        FoodCategory(
            "Coffee",
            "https://images.unsplash.com/photo-1509042239860-f550ce710b93?q=80&w=300&fit=crop",
            "₺25"
        )
    )

    val restaurants = listOf(
        Restaurant(
            "Pizza Heaven",
            "Pizza, Italian, Fast Food",
            4.8f,
            213,
            "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?q=80&w=600&auto=format",
            "20-30 min",
            "1.2 km"
        ),
        Restaurant(
            "Burger Joint",
            "Burgers, American, Fries",
            4.5f,
            187,
            "https://images.unsplash.com/photo-1552566626-52f8b828add9?q=80&w=600&auto=format",
            "15-25 min",
            "0.8 km"
        ),
        Restaurant(
            "Sushi Palace",
            "Sushi, Japanese, Seafood",
            4.9f,
            342,
            "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?q=80&w=600&auto=format",
            "25-35 min",
            "1.5 km"
        ),
        Restaurant(
            "Pasta Paradise",
            "Italian, Pasta, Wine",
            4.7f,
            156,
            "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?q=80&w=600&auto=format",
            "20-35 min",
            "2.0 km"
        )
    )

    Scaffold(
        containerColor = white,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = white
                ),
                title = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "ANLIK KONUM",
                            color = primary,
                            fontSize = 14.sp,
                            fontWeight = Bold,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ankara, Kızılay",
                                color = gray,
                                fontSize = 12.sp,
                                lineHeight = 20.sp,
                                fontWeight = Bold,
                                modifier = Modifier.padding(start = 16.dp)
                            )

                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Menu",
                                tint = gray,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(20.dp)
                            )
                        }
                    }

                },
                navigationIcon = {
                    IconButton(
                        onClick = { /* Do something */ },
                        modifier = Modifier
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.menu_left),
                            contentDescription = "Menu",
                            colorFilter = ColorFilter.tint(primary),
                            modifier = Modifier
                                .padding(8.dp)
                                .padding(start = 2.dp)
                        )
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 16.dp)) {
                        IconButton(
                            onClick = { /* Do something */ },
                            modifier = Modifier
                                .padding(1.dp)
                                .background(darkBlue, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.shopping_cart),
                                contentDescription = "Menu",
                                colorFilter = ColorFilter.tint(white),
                                modifier = Modifier.padding(12.dp)
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
        }
    ) { paddingValues ->

        var searchText by remember { mutableStateOf("") }
        var isSearchFocused by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.size(16.dp))

            SearchTextField(
                text = searchText,
                onValueChange = {
                    searchText = it
                },
                placeholder = "Search dishes, restaurants",
            )

            Spacer(modifier = Modifier.size(24.dp))

            // Categories Section
            Column(
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Text(
                            text = "Categories",
                            color = black,
                            fontSize = 18.sp,
                            fontWeight = Bold
                        )
                    }
                    TextButton(
                        onClick = {},
                        colors = ButtonDefaults.textButtonColors().copy(
                            containerColor = Color.Transparent,
                            contentColor = gray,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = gray,
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "See All",
                                color = gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Icon(
                                painter = painterResource(Res.drawable.arrow_right),
                                contentDescription = "arrow",
                                tint = gray,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(12.dp)                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                ) {
                    items(categories) { category ->
                        CategoryItem(category)
                    }
                }
            }

            Spacer(modifier = Modifier.size(24.dp))

            // Nearby Restaurants Section
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Text(
                            text = "Nearby Restaurants",
                            color = black,
                            fontSize = 18.sp,
                            fontWeight = Bold
                        )
                    }

                    TextButton(
                        onClick = {},
                        colors = ButtonDefaults.textButtonColors().copy(
                            containerColor = Color.Transparent,
                            contentColor = gray,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = gray,
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "See All",
                                color = gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Icon(
                                painter = painterResource(Res.drawable.arrow_right),
                                contentDescription = "Star",
                                tint = gray,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(12.dp)
                            )
                        }
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                val sortedRestaurants = restaurants.sortedBy { restaurant ->
                    restaurant.distance.split(" ")[0].toFloatOrNull() ?: Float.MAX_VALUE
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(sortedRestaurants) { restaurant ->
                        RestaurantCard(restaurant, onCardSelected = {
                            navController.navigate(Screen.TenantMenu.route)
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

