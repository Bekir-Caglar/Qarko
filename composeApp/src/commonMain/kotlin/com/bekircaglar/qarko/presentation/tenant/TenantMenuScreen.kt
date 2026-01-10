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
import com.bekircaglar.qarko.data.model.Allergen
import com.bekircaglar.qarko.data.model.CustomizationGroup
import com.bekircaglar.qarko.data.model.CustomizationOption
import com.bekircaglar.qarko.data.model.CustomizationType
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.data.model.FoodType
import com.bekircaglar.qarko.data.model.Ingredient
import com.bekircaglar.qarko.data.model.RemovableItem
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
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TenantMenuScreen(navController: NavController) {
    val menuCategories = listOf(
        "Favoriler", "Başlangıçlar", "Pizzalar", "Burgerler", "Kebaplar",
        "Ana Yemekler", "Çorbalar", "Makarnalar", "Sandviçler", "Dürümler",
        "Deniz Ürünleri", "Salatalar", "Kahvaltı", "İçecekler", "Sıcak İçecekler",
        "Alkollü İçecekler", "Tatlılar", "Dondurma", "Waffle", "Nargile",
        "Atıştırmalıklar", "Yan Ürünler"
    )

    // Sepet durumunu takip etmek için state
    val cartItems = remember { mutableStateMapOf<String, Int>() }
    val totalCartCount = cartItems.values.sum()

    val categorizedFoods = remember {
        mapOf(
            "Favoriler" to listOf(
                FoodItem(
                    id = "fav1",
                    name = "Margherita Pizza",
                    imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?q=80&w=400&fit=crop",
                    price = "₺120",
                    info = "Mozzarella, domates, fesleğen",
                    category = "Favoriler",
                    rating = 4.8f,
                    ratingCount = 234,
                    foodType = FoodType.PIZZA,
                    ingredients = listOf(
                        Ingredient("1", "Mozzarella", "🧀", true),
                        Ingredient("2", "Domates Sosu", "🍅", true),
                        Ingredient("3", "Fesleğen", "🌿"),
                        Ingredient("4", "Zeytinyağı", "🫒")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "size",
                            name = "Boyut",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("s", "Küçük", "₺0", true),
                                CustomizationOption("m", "Orta", "₺20"),
                                CustomizationOption("l", "Büyük", "₺40")
                            )
                        ),
                        CustomizationGroup(
                            id = "dough",
                            name = "Hamur Tipi",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("thin", "İnce Hamur", "₺0", true),
                                CustomizationOption("normal", "Normal Hamur", "₺0"),
                                CustomizationOption("thick", "Kalın Hamur", "₺10")
                            )
                        ),
                        CustomizationGroup(
                            id = "extras",
                            name = "Ekstra Malzemeler",
                            type = CustomizationType.MULTI_SELECT,
                            options = listOf(
                                CustomizationOption("cheese", "Ekstra Peynir", "₺15"),
                                CustomizationOption("olive", "Zeytin", "₺10"),
                                CustomizationOption("mushroom", "Mantar", "₺12"),
                                CustomizationOption("pepper", "Biber", "₺8")
                            )
                        )
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                ),
                FoodItem(
                    id = "fav2",
                    name = "Cheeseburger",
                    imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?q=80&w=400&fit=crop",
                    price = "₺95",
                    info = "Dana eti, cheddar, turşu",
                    category = "Favoriler",
                    rating = 4.6f,
                    ratingCount = 189,
                    foodType = FoodType.BURGER,
                    ingredients = listOf(
                        Ingredient("1", "Dana Köfte", "🥩", true),
                        Ingredient("2", "Cheddar", "🧀", true),
                        Ingredient("3", "Marul", "🥬"),
                        Ingredient("4", "Domates", "🍅"),
                        Ingredient("5", "Soğan", "🧅"),
                        Ingredient("6", "Turşu", "🥒")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "cooking",
                            name = "Et Pişirme Derecesi",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("rare", "Az Pişmiş", "₺0"),
                                CustomizationOption("medium", "Orta", "₺0", true),
                                CustomizationOption("well", "Çok Pişmiş", "₺0")
                            )
                        ),
                        CustomizationGroup(
                            id = "sauce",
                            name = "Sos Seçimi",
                            type = CustomizationType.MULTI_SELECT,
                            options = listOf(
                                CustomizationOption("ketchup", "Ketçap", "₺0", true),
                                CustomizationOption("mayo", "Mayonez", "₺0", true),
                                CustomizationOption("bbq", "BBQ Sos", "₺5"),
                                CustomizationOption("spicy", "Acı Sos", "₺5")
                            )
                        )
                    ),
                    removableItems = listOf(
                        RemovableItem("r1", "Soğan"),
                        RemovableItem("r2", "Turşu"),
                        RemovableItem("r3", "Marul"),
                        RemovableItem("r4", "Domates")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY, Allergen.EGGS)
                )
            ),
            "Başlangıçlar" to listOf(
                FoodItem(
                    id = "start1",
                    name = "Bruschetta",
                    imageUrl = "https://images.unsplash.com/photo-1506280754576-f6fa8a873550?q=80&w=400&fit=crop",
                    price = "₺75",
                    info = "Domates, sarımsak, fesleğen",
                    category = "Başlangıçlar",
                    rating = 4.5f,
                    ratingCount = 98,
                    foodType = FoodType.STARTER,
                    ingredients = listOf(
                        Ingredient("1", "Ekmek", "🍞", true),
                        Ingredient("2", "Domates", "🍅", true),
                        Ingredient("3", "Sarımsak", "🧄"),
                        Ingredient("4", "Fesleğen", "🌿")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "portion",
                            name = "Porsiyon",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("4pc", "4 Parça", "₺0", true),
                                CustomizationOption("6pc", "6 Parça", "₺25")
                            )
                        )
                    ),
                    allergens = listOf(Allergen.GLUTEN)
                ),
                FoodItem(
                    id = "start2",
                    name = "Sarımsaklı Ekmek",
                    imageUrl = "https://images.unsplash.com/photo-1573140247632-f8fd74997d5c?q=80&w=400&fit=crop",
                    price = "₺60",
                    info = "Sarımsak, tereyağı, maydanoz",
                    category = "Başlangıçlar",
                    rating = 4.3f,
                    ratingCount = 76,
                    foodType = FoodType.STARTER,
                    ingredients = listOf(
                        Ingredient("1", "Ekmek", "🍞", true),
                        Ingredient("2", "Sarımsak", "🧄", true),
                        Ingredient("3", "Tereyağı", "🧈"),
                        Ingredient("4", "Maydanoz", "🌿")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                )
            ),
            "Pizzalar" to listOf(
                FoodItem(
                    id = "pizza1",
                    name = "Margherita Pizza",
                    imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?q=80&w=400&fit=crop",
                    price = "₺120",
                    info = "Mozzarella, domates, fesleğen",
                    category = "Pizzalar",
                    rating = 4.8f,
                    ratingCount = 234,
                    foodType = FoodType.PIZZA,
                    ingredients = listOf(
                        Ingredient("1", "Mozzarella", "🧀", true),
                        Ingredient("2", "Domates Sosu", "🍅", true),
                        Ingredient("3", "Fesleğen", "🌿")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "size",
                            name = "Boyut",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("s", "Küçük", "₺0", true),
                                CustomizationOption("m", "Orta", "₺20"),
                                CustomizationOption("l", "Büyük", "₺40")
                            )
                        ),
                        CustomizationGroup(
                            id = "dough",
                            name = "Hamur Tipi",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("thin", "İnce Hamur", "₺0", true),
                                CustomizationOption("normal", "Normal Hamur", "₺0"),
                                CustomizationOption("thick", "Kalın Hamur", "₺10")
                            )
                        )
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                ),
                FoodItem(
                    id = "pizza2",
                    name = "Pepperoni Pizza",
                    imageUrl = "https://images.unsplash.com/photo-1534308983496-4fabb1a015ee?q=80&w=400&fit=crop",
                    price = "₺140",
                    info = "Pepperoni, mozzarella, domates",
                    category = "Pizzalar",
                    rating = 4.7f,
                    ratingCount = 198,
                    foodType = FoodType.PIZZA,
                    ingredients = listOf(
                        Ingredient("1", "Pepperoni", "🍖", true),
                        Ingredient("2", "Mozzarella", "🧀", true),
                        Ingredient("3", "Domates Sosu", "🍅")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "size",
                            name = "Boyut",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("s", "Küçük", "₺0", true),
                                CustomizationOption("m", "Orta", "₺20"),
                                CustomizationOption("l", "Büyük", "₺40")
                            )
                        ),
                        CustomizationGroup(
                            id = "dough",
                            name = "Hamur Tipi",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("thin", "İnce Hamur", "₺0", true),
                                CustomizationOption("normal", "Normal Hamur", "₺0"),
                                CustomizationOption("thick", "Kalın Hamur", "₺10")
                            )
                        )
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                ),
                FoodItem(
                    id = "pizza3",
                    name = "Vejetaryen Pizza",
                    imageUrl = "https://images.unsplash.com/photo-1593560708920-61dd98c46a4e?q=80&w=400&fit=crop",
                    price = "₺130",
                    info = "Mantar, biber, soğan, zeytin",
                    category = "Pizzalar",
                    rating = 4.5f,
                    ratingCount = 142,
                    foodType = FoodType.PIZZA,
                    ingredients = listOf(
                        Ingredient("1", "Mantar", "🍄", true),
                        Ingredient("2", "Biber", "🫑", true),
                        Ingredient("3", "Soğan", "🧅"),
                        Ingredient("4", "Zeytin", "🫒")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "size",
                            name = "Boyut",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("s", "Küçük", "₺0", true),
                                CustomizationOption("m", "Orta", "₺20"),
                                CustomizationOption("l", "Büyük", "₺40")
                            )
                        ),
                        CustomizationGroup(
                            id = "dough",
                            name = "Hamur Tipi",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("thin", "İnce Hamur", "₺0", true),
                                CustomizationOption("normal", "Normal Hamur", "₺0"),
                                CustomizationOption("thick", "Kalın Hamur", "₺10")
                            )
                        )
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                )
            ),
            "Burgerler" to listOf(
                FoodItem(
                    id = "burger1",
                    name = "Cheeseburger",
                    imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?q=80&w=400&fit=crop",
                    price = "₺95",
                    info = "Dana eti, cheddar, turşu",
                    category = "Burgerler",
                    rating = 4.6f,
                    ratingCount = 189,
                    foodType = FoodType.BURGER,
                    ingredients = listOf(
                        Ingredient("1", "Dana Köfte", "🥩", true),
                        Ingredient("2", "Cheddar", "🧀", true),
                        Ingredient("3", "Marul", "🥬"),
                        Ingredient("4", "Domates", "🍅")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "cooking",
                            name = "Et Pişirme Derecesi",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("rare", "Az Pişmiş", "₺0"),
                                CustomizationOption("medium", "Orta", "₺0", true),
                                CustomizationOption("well", "Çok Pişmiş", "₺0")
                            )
                        )
                    ),
                    removableItems = listOf(
                        RemovableItem("r1", "Soğan"),
                        RemovableItem("r2", "Turşu"),
                        RemovableItem("r3", "Marul")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                ),
                FoodItem(
                    id = "burger2",
                    name = "Tavuk Burger",
                    imageUrl = "https://images.unsplash.com/photo-1513185041617-8ab03f83d6c5?q=80&w=400&fit=crop",
                    price = "₺85",
                    info = "Tavuk göğsü, marul, domates",
                    category = "Burgerler",
                    rating = 4.4f,
                    ratingCount = 156,
                    foodType = FoodType.BURGER,
                    ingredients = listOf(
                        Ingredient("1", "Tavuk Göğsü", "🍗", true),
                        Ingredient("2", "Marul", "🥬"),
                        Ingredient("3", "Domates", "🍅"),
                        Ingredient("4", "Mayonez", "🥄")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY, Allergen.EGGS)
                ),
                FoodItem(
                    id = "burger3",
                    name = "Vejetaryen Burger",
                    imageUrl = "https://images.unsplash.com/photo-1520072959219-c595dc870360?q=80&w=400&fit=crop",
                    price = "₺80",
                    info = "Sebze köftesi, avokado, roka",
                    category = "Burgerler",
                    rating = 4.3f,
                    ratingCount = 89,
                    foodType = FoodType.BURGER,
                    ingredients = listOf(
                        Ingredient("1", "Sebze Köftesi", "🥕", true),
                        Ingredient("2", "Avokado", "🥑"),
                        Ingredient("3", "Roka", "🥬"),
                        Ingredient("4", "Domates", "🍅")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                )
            ),
            "Kebaplar" to listOf(
                FoodItem(
                    id = "kebap1",
                    name = "Adana Kebap",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺110",
                    info = "Baharatlı kıyma, şişte kebap",
                    category = "Kebaplar",
                    rating = 4.7f,
                    ratingCount = 210,
                    foodType = FoodType.KEBAB,
                    ingredients = listOf(
                        Ingredient("1", "Kıyma", "🥩", true),
                        Ingredient("2", "Biber Salçası", "🌶️", true),
                        Ingredient("3", "Soğan", "🧅"),
                        Ingredient("4", "Domates", "🍅")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "spiciness",
                            name = "Acılık Seviyesi",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("mild", "Açık", "₺0", true),
                                CustomizationOption("medium", "Orta", "₺0"),
                                CustomizationOption("spicy", "Acı", "₺0")
                            )
                        )
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                ),
                FoodItem(
                    id = "kebap2",
                    name = "Urfa Kebap",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺120",
                    info = "Baharatlı kıyma, şişte kebap",
                    category = "Kebaplar",
                    rating = 4.8f,
                    ratingCount = 198,
                    foodType = FoodType.KEBAB,
                    ingredients = listOf(
                        Ingredient("1", "Kıyma", "🥩", true),
                        Ingredient("2", "Urfa Biberi", "🌶️", true),
                        Ingredient("3", "Soğan", "🧅"),
                        Ingredient("4", "Domates", "🍅")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                ),
                FoodItem(
                    id = "kebap3",
                    name = "İskender Kebap",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺140",
                    info = "Döner eti, pide ekmeği, domates sosu",
                    category = "Kebaplar",
                    rating = 4.9f,
                    ratingCount = 245,
                    foodType = FoodType.KEBAB,
                    ingredients = listOf(
                        Ingredient("1", "Döner Eti", "🥩", true),
                        Ingredient("2", "Pide Ekmek", "🍞", true),
                        Ingredient("3", "Domates Sosu", "🍅"),
                        Ingredient("4", "Yoğurt", "🥛")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "portion",
                            name = "Porsiyon",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("normal", "Normal", "₺0", true),
                                CustomizationOption("large", "Büyük", "₺20")
                            )
                        ),
                        CustomizationGroup(
                            id = "bread_type",
                            name = "Ekmek Tipi",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("pide", "Pide", "₺0", true),
                                CustomizationOption("lavash", "Lavaş", "₺5")
                            )
                        )
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                )
            ),
            "Ana Yemekler" to listOf(
                FoodItem(
                    id = "main1",
                    name = "Izgara Tavuk",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺90",
                    info = "Izgara tavuk göğsü, sebzeler",
                    category = "Ana Yemekler",
                    rating = 4.6f,
                    ratingCount = 150,
                    foodType = FoodType.MAIN_COURSE,
                    ingredients = listOf(
                        Ingredient("1", "Tavuk Göğsü", "🍗", true),
                        Ingredient("2", "Zeytinyağı", "🫒", true),
                        Ingredient("3", "Kekik", "🌿"),
                        Ingredient("4", "Fesleğen", "🌿")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "side_dish",
                            name = "Yan Ürün Seçimi",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("rice", "Pilav", "₺0", true),
                                CustomizationOption("potato", "Patates Püresi", "₺10"),
                                CustomizationOption("vegetable", "Sebze Tabağı", "₺15")
                            )
                        )
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                ),
                FoodItem(
                    id = "main2",
                    name = "Beef Stroganoff",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺130",
                    info = "Dana eti, mantar, krema sosu",
                    category = "Ana Yemekler",
                    rating = 4.7f,
                    ratingCount = 175,
                    foodType = FoodType.MAIN_COURSE,
                    ingredients = listOf(
                        Ingredient("1", "Dana Eti", "🥩", true),
                        Ingredient("2", "Mantar", "🍄", true),
                        Ingredient("3", "Krema", "🥛"),
                        Ingredient("4", "Soğan", "🧅")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                )
            ),
            "Çorbalar" to listOf(
                FoodItem(
                    id = "soup1",
                    name = "Mercimek Çorbası",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺40",
                    info = "Kırmızı mercimek, soğan, havuç",
                    category = "Çorbalar",
                    rating = 4.5f,
                    ratingCount = 120,
                    foodType = FoodType.SOUP,
                    ingredients = listOf(
                        Ingredient("1", "Kırmızı Mercimek", "🫘", true),
                        Ingredient("2", "Soğan", "🧅", true),
                        Ingredient("3", "Havuç", "🥕"),
                        Ingredient("4", "Sarımsak", "🧄")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "spiciness",
                            name = "Acılık Seviyesi",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("mild", "Açık", "₺0", true),
                                CustomizationOption("medium", "Orta", "₺0"),
                                CustomizationOption("spicy", "Acı", "₺0")
                            )
                        )
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                ),
                FoodItem(
                    id = "soup2",
                    name = "Domates Çorbası",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺35",
                    info = "Domates, biber, soğan, sarımsak",
                    category = "Çorbalar",
                    rating = 4.4f,
                    ratingCount = 110,
                    foodType = FoodType.SOUP,
                    ingredients = listOf(
                        Ingredient("1", "Domates", "🍅", true),
                        Ingredient("2", "Biber", "🫑", true),
                        Ingredient("3", "Soğan", "🧅"),
                        Ingredient("4", "Sarımsak", "🧄")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                )
            ),
            "Makarnalar" to listOf(
                FoodItem(
                    id = "pasta1",
                    name = "Spaghetti Bolognese",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺70",
                    info = "Spaghetti, kıyma, domates sosu",
                    category = "Makarnalar",
                    rating = 4.6f,
                    ratingCount = 140,
                    foodType = FoodType.PASTA,
                    ingredients = listOf(
                        Ingredient("1", "Spaghetti", "🍝", true),
                        Ingredient("2", "Kıyma", "🥩", true),
                        Ingredient("3", "Domates Sosu", "🍅"),
                        Ingredient("4", "Parmesan", "🧀")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "pasta_type",
                            name = "Makarna Tipi",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("spaghetti", "Spaghetti", "₺0", true),
                                CustomizationOption("penne", "Penne", "₺0"),
                                CustomizationOption("fusilli", "Fusilli", "₺0")
                            )
                        )
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                ),
                FoodItem(
                    id = "pasta2",
                    name = "Fettuccine Alfredo",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺80",
                    info = "Fettuccine, krema, parmesan peyniri",
                    category = "Makarnalar",
                    rating = 4.7f,
                    ratingCount = 130,
                    foodType = FoodType.PASTA,
                    ingredients = listOf(
                        Ingredient("1", "Fettuccine", "🍝", true),
                        Ingredient("2", "Krema", "🥛", true),
                        Ingredient("3", "Parmesan", "🧀"),
                        Ingredient("4", "Sarımsak", "🧄")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                )
            ),
            "Sandviçler" to listOf(
                FoodItem(
                    id = "sandwich1",
                    name = "Tavuk Sandviç",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺50",
                    info = "Tavuk, marul, domates, mayonez",
                    category = "Sandviçler",
                    rating = 4.5f,
                    ratingCount = 100,
                    foodType = FoodType.SANDWICH,
                    ingredients = listOf(
                        Ingredient("1", "Tavuk", "🍗", true),
                        Ingredient("2", "Marul", "🥬"),
                        Ingredient("3", "Domates", "🍅"),
                        Ingredient("4", "Mayonez", "🥄")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "bread_type",
                            name = "Ekmek Tipi",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("white", "Beyaz Ekmek", "₺0", true),
                                CustomizationOption("whole_grain", "Tam Buğday Ekmeği", "₺5"),
                                CustomizationOption("ciabatta", "Ciabatta", "₺10")
                            )
                        )
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                ),
                FoodItem(
                    id = "sandwich2",
                    name = "Köfte Sandviç",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺55",
                    info = "Köfte, marul, domates, soğan",
                    category = "Sandviçler",
                    rating = 4.6f,
                    ratingCount = 90,
                    foodType = FoodType.SANDWICH,
                    ingredients = listOf(
                        Ingredient("1", "Köfte", "🥩", true),
                        Ingredient("2", "Marul", "🥬"),
                        Ingredient("3", "Domates", "🍅"),
                        Ingredient("4", "Soğan", "🧅")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                )
            ),
            "Dürümler" to listOf(
                FoodItem(
                    id = "durum1",
                    name = "Tavuk Dürüm",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺50",
                    info = "Tavuk, marul, domates, dürüm ekmeği",
                    category = "Dürümler",
                    rating = 4.5f,
                    ratingCount = 100,
                    foodType = FoodType.WRAP,
                    ingredients = listOf(
                        Ingredient("1", "Tavuk", "🍗", true),
                        Ingredient("2", "Marul", "🥬"),
                        Ingredient("3", "Domates", "🍅"),
                        Ingredient("4", "Dürüm Ekmeği", "🌯")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "spiciness",
                            name = "Acılık Seviyesi",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("mild", "Açık", "₺0", true),
                                CustomizationOption("medium", "Orta", "₺0"),
                                CustomizationOption("spicy", "Acı", "₺0")
                            )
                        )
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                ),
                FoodItem(
                    id = "durum2",
                    name = "Kuzu Dürüm",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺60",
                    info = "Kuzu eti, marul, domates, dürüm ekmeği",
                    category = "Dürümler",
                    rating = 4.6f,
                    ratingCount = 90,
                    foodType = FoodType.WRAP,
                    ingredients = listOf(
                        Ingredient("1", "Kuzu Eti", "🥩", true),
                        Ingredient("2", "Marul", "🥬"),
                        Ingredient("3", "Domates", "🍅"),
                        Ingredient("4", "Dürüm Ekmeği", "🌯")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                )
            ),
            "Deniz Ürünleri" to listOf(
                FoodItem(
                    id = "seafood1",
                    name = "Levrek Izgara",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺150",
                    info = "Izgara levrek, zeytinyağlı sebzeler",
                    category = "Deniz Ürünleri",
                    rating = 4.8f,
                    ratingCount = 200,
                    foodType = FoodType.SEAFOOD,
                    ingredients = listOf(
                        Ingredient("1", "Levrek", "🐟", true),
                        Ingredient("2", "Zeytinyağı", "🫒", true),
                        Ingredient("3", "Limon", "🍋"),
                        Ingredient("4", "Dereotu", "🌿")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "side_dish",
                            name = "Yan Ürün Seçimi",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("rice", "Pilav", "₺0", true),
                                CustomizationOption("potato", "Patates Püresi", "₺10"),
                                CustomizationOption("vegetable", "Sebze Tabağı", "₺15")
                            )
                        )
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY, Allergen.FISH)
                ),
                FoodItem(
                    id = "seafood2",
                    name = "Karides Güveç",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺160",
                    info = "Karides, domates, biber, soğan, baharatlar",
                    category = "Deniz Ürünleri",
                    rating = 4.9f,
                    ratingCount = 180,
                    foodType = FoodType.SEAFOOD,
                    ingredients = listOf(
                        Ingredient("1", "Karides", "🦐", true),
                        Ingredient("2", "Domates", "🍅", true),
                        Ingredient("3", "Biber", "🫑"),
                        Ingredient("4", "Soğan", "🧅")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY, Allergen.SHELLFISH)
                )
            ),
            "Salatalar" to listOf(
                FoodItem(
                    id = "salad1",
                    name = "Sezar Salata",
                    imageUrl = "https://images.unsplash.com/photo-1550304943-4f24f54ddde9?q=80&w=400&fit=crop",
                    price = "₺70",
                    info = "Marul, tavuk, kruton, parmesan",
                    category = "Salatalar",
                    rating = 4.5f,
                    ratingCount = 112,
                    foodType = FoodType.SALAD,
                    ingredients = listOf(
                        Ingredient("1", "Marul", "🥬", true),
                        Ingredient("2", "Tavuk", "🍗", true),
                        Ingredient("3", "Kruton", "🍞"),
                        Ingredient("4", "Parmesan", "🧀")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "sauce",
                            name = "Sos Seçimi",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("caesar", "Sezar Sos", "₺0", true),
                                CustomizationOption("ranch", "Ranch Sos", "₺0"),
                                CustomizationOption("balsamic", "Balsamik", "₺5")
                            )
                        )
                    )
                ),
                FoodItem(
                    id = "salad2",
                    name = "Yunan Salata",
                    imageUrl = "https://images.unsplash.com/photo-1644704170910-a0cdf183649b?q=80&w=400&fit=crop",
                    price = "₺65",
                    info = "Domates, salatalık, zeytin, peynir",
                    category = "Salatalar",
                    rating = 4.4f,
                    ratingCount = 87,
                    foodType = FoodType.SALAD,
                    ingredients = listOf(
                        Ingredient("1", "Domates", "🍅", true),
                        Ingredient("2", "Salatalık", "🥒", true),
                        Ingredient("3", "Zeytin", "🫒"),
                        Ingredient("4", "Feta Peyniri", "🧀")
                    )
                )
            ),
            "Kahvaltı" to listOf(
                FoodItem(
                    id = "breakfast1",
                    name = "Kahvaltı Tabağı",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺80",
                    info = "Sucuk, pastırma, beyaz peynir, zeytin",
                    category = "Kahvaltı",
                    rating = 4.6f,
                    ratingCount = 95,
                    foodType = FoodType.BREAKFAST,
                    ingredients = listOf(
                        Ingredient("1", "Sucuk", "🥓", true),
                        Ingredient("2", "Pastırma", "🥓", true),
                        Ingredient("3", "Beyaz Peynir", "🧀"),
                        Ingredient("4", "Zeytin", "🫒")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY, Allergen.EGGS)
                ),
                FoodItem(
                    id = "breakfast2",
                    name = "Omlet",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺50",
                    info = "Yumurtalar, beyaz peynir, zeytin, domates",
                    category = "Kahvaltı",
                    rating = 4.5f,
                    ratingCount = 85,
                    foodType = FoodType.BREAKFAST,
                    ingredients = listOf(
                        Ingredient("1", "Yumurta", "🥚", true),
                        Ingredient("2", "Beyaz Peynir", "🧀", true),
                        Ingredient("3", "Zeytin", "🫒"),
                        Ingredient("4", "Domates", "🍅")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY, Allergen.EGGS)
                )
            ),
            "İçecekler" to listOf(
                FoodItem(
                    id = "drink1",
                    name = "Limonata",
                    imageUrl = "https://images.unsplash.com/photo-1621263764928-df1444c5e859?q=80&w=400&fit=crop",
                    price = "₺35",
                    info = "Taze sıkılmış limon suyu, nane",
                    category = "İçecekler",
                    rating = 4.7f,
                    ratingCount = 203,
                    foodType = FoodType.DRINK,
                    ingredients = listOf(
                        Ingredient("1", "Limon", "🍋", true),
                        Ingredient("2", "Şeker", "🧂"),
                        Ingredient("3", "Nane", "🌿"),
                        Ingredient("4", "Su", "💧")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "size",
                            name = "Boyut",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("s", "Küçük (300ml)", "₺0", true),
                                CustomizationOption("m", "Orta (500ml)", "₺10"),
                                CustomizationOption("l", "Büyük (700ml)", "₺20")
                            )
                        ),
                        CustomizationGroup(
                            id = "ice",
                            name = "Buz Tercihi",
                            type = CustomizationType.SINGLE_SELECT,
                            options = listOf(
                                CustomizationOption("normal", "Normal Buz", "₺0", true),
                                CustomizationOption("less", "Az Buz", "₺0"),
                                CustomizationOption("no", "Buzsuz", "₺0")
                            )
                        ),
                        CustomizationGroup(
                            id = "sugar",
                            name = "Şeker Miktarı",
                            type = CustomizationType.SINGLE_SELECT,
                            options = listOf(
                                CustomizationOption("normal", "Normal", "₺0", true),
                                CustomizationOption("less", "Az Şekerli", "₺0"),
                                CustomizationOption("no", "Şekersiz", "₺0")
                            )
                        )
                    )
                ),
                FoodItem(
                    id = "drink2",
                    name = "Çeşitli Sodalar",
                    imageUrl = "https://images.unsplash.com/photo-1596803244897-c7dec84a551e?q=80&w=400&fit=crop",
                    price = "₺20",
                    info = "Cola, Gazoz, Meyve Sodaları",
                    category = "İçecekler",
                    rating = 4.2f,
                    ratingCount = 156,
                    foodType = FoodType.DRINK,
                    ingredients = listOf(
                        Ingredient("1", "Gazlı İçecek", "🥤", true),
                        Ingredient("2", "Şeker", "🧂"),
                        Ingredient("3", "Karbonat", "💨")
                    )
                )
            ),
            "Sıcak İçecekler" to listOf(
                FoodItem(
                    id = "hot_drink1",
                    name = "Türk Kahvesi",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺25",
                    info = "Kavrulmuş Türk kahvesi, su",
                    category = "Sıcak İçecekler",
                    rating = 4.8f,
                    ratingCount = 220,
                    foodType = FoodType.HOT_DRINK,
                    ingredients = listOf(
                        Ingredient("1", "Türk Kahvesi", "☕", true),
                        Ingredient("2", "Su", "💧", true),
                        Ingredient("3", "Şeker", "🧂")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                ),
                FoodItem(
                    id = "hot_drink2",
                    name = "Çay",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺15",
                    info = "Siyah çay, su",
                    category = "Sıcak İçecekler",
                    rating = 4.7f,
                    ratingCount = 180,
                    foodType = FoodType.HOT_DRINK,
                    ingredients = listOf(
                        Ingredient("1", "Çay", "🍵", true),
                        Ingredient("2", "Su", "💧", true),
                        Ingredient("3", "Limon", "🍋")
                    )
                )
            ),
            "Alkollü İçecekler" to listOf(
                FoodItem(
                    id = "alcohol1",
                    name = "Bira",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺30",
                    info = "Soğuk bira",
                    category = "Alkollü İçecekler",
                    rating = 4.5f,
                    ratingCount = 200,
                    foodType = FoodType.ALCOHOLIC,
                    ingredients = listOf(
                        Ingredient("1", "Bira", "🍺", true),
                        Ingredient("2", "Arpa", "🌾"),
                        Ingredient("3", "Şerbetçiotu", "🌿")
                    )
                ),
                FoodItem(
                    id = "alcohol2",
                    name = "Şarap",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺70",
                    info = "Kırmızı veya beyaz şarap",
                    category = "Alkollü İçecekler",
                    rating = 4.6f,
                    ratingCount = 190,
                    foodType = FoodType.ALCOHOLIC,
                    ingredients = listOf(
                        Ingredient("1", "Şarap", "🍷", true),
                        Ingredient("2", "Üzüm", "🍇", true),
                        Ingredient("3", "Alkol", "🥃")
                    )
                )
            ),
            "Tatlılar" to listOf(
                FoodItem(
                    id = "dessert1",
                    name = "Tiramisu",
                    imageUrl = "https://images.unsplash.com/photo-1551024601-bec78aea704b?q=80&w=400&fit=crop",
                    price = "₺60",
                    info = "Kahveli, maccarone, kakao",
                    category = "Tatlılar",
                    rating = 4.9f,
                    ratingCount = 178,
                    foodType = FoodType.DESSERT,
                    ingredients = listOf(
                        Ingredient("1", "Mascarpone", "🧀", true),
                        Ingredient("2", "Kahve", "☕", true),
                        Ingredient("3", "Bisküvi", "🍪"),
                        Ingredient("4", "Kakao", "🍫")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "extras",
                            name = "Ekstralar",
                            type = CustomizationType.MULTI_SELECT,
                            options = listOf(
                                CustomizationOption("cream", "Ekstra Krema", "₺10"),
                                CustomizationOption("chocolate", "Çikolata Sos", "₺8")
                            )
                        )
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY, Allergen.EGGS)
                ),
                FoodItem(
                    id = "dessert2",
                    name = "Çikolatalı Sufle",
                    imageUrl = "https://images.unsplash.com/photo-1579306194872-64d3b7bac4c2?q=80&w=400&fit=crop",
                    price = "₺70",
                    info = "Sıcak çikolatalı, dondurma ile",
                    category = "Tatlılar",
                    rating = 4.8f,
                    ratingCount = 145,
                    foodType = FoodType.DESSERT,
                    ingredients = listOf(
                        Ingredient("1", "Çikolata", "🍫", true),
                        Ingredient("2", "Yumurta", "🥚", true),
                        Ingredient("3", "Un", "🌾"),
                        Ingredient("4", "Dondurma", "🍨")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY, Allergen.EGGS)
                )
            ),
            "Dondurma" to listOf(
                FoodItem(
                    id = "ice_cream1",
                    name = "Vanilyalı Dondurma",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺40",
                    info = "Klasik vanilya dondurma",
                    category = "Dondurma",
                    rating = 4.5f,
                    ratingCount = 120,
                    foodType = FoodType.ICE_CREAM,
                    ingredients = listOf(
                        Ingredient("1", "Süt", "🥛", true),
                        Ingredient("2", "Şeker", "🧂", true),
                        Ingredient("3", "Vanilya", "🌿"),
                        Ingredient("4", "Krema", "🥛")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                ),
                FoodItem(
                    id = "ice_cream2",
                    name = "Çikolatalı Dondurma",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺45",
                    info = "Zengin çikolata dondurma",
                    category = "Dondurma",
                    rating = 4.6f,
                    ratingCount = 110,
                    foodType = FoodType.ICE_CREAM,
                    ingredients = listOf(
                        Ingredient("1", "Çikolata", "🍫", true),
                        Ingredient("2", "Süt", "🥛", true),
                        Ingredient("3", "Şeker", "🧂"),
                        Ingredient("4", "Krema", "🥛")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                )
            ),
            "Waffle" to listOf(
                FoodItem(
                    id = "waffle1",
                    name = "Klasik Waffle",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺50",
                    info = "Klasik waffle, dondurma, çikolata sosu",
                    category = "Waffle",
                    rating = 4.7f,
                    ratingCount = 130,
                    foodType = FoodType.WAFFLE,
                    ingredients = listOf(
                        Ingredient("1", "Waffle", "🧇", true),
                        Ingredient("2", "Dondurma", "🍨", true),
                        Ingredient("3", "Çikolata Sosu", "🍫"),
                        Ingredient("4", "Çilek", "🍓")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "toppings",
                            name = "Topping Seçimi",
                            type = CustomizationType.MULTI_SELECT,
                            options = listOf(
                                CustomizationOption("strawberry", "Çilek", "₺5"),
                                CustomizationOption("banana", "Muz", "₺5"),
                                CustomizationOption("nuts", "Fındık", "₺8")
                            )
                        )
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                ),
                FoodItem(
                    id = "waffle2",
                    name = "Meyveli Waffle",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺55",
                    info = "Meyveler, dondurma, çikolata sosu",
                    category = "Waffle",
                    rating = 4.8f,
                    ratingCount = 125,
                    foodType = FoodType.WAFFLE,
                    ingredients = listOf(
                        Ingredient("1", "Waffle", "🧇", true),
                        Ingredient("2", "Çilek", "🍓", true),
                        Ingredient("3", "Muz", "🍌"),
                        Ingredient("4", "Dondurma", "🍨")
                    ),
                    allergens = listOf(Allergen.GLUTEN, Allergen.DAIRY)
                )
            ),
            "Nargile" to listOf(
                FoodItem(
                    id = "hookah1",
                    name = "Elmas Nargile",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺100",
                    info = "Elmas nargile, çeşitli aromalar",
                    category = "Nargile",
                    rating = 4.6f,
                    ratingCount = 90,
                    foodType = FoodType.HOOKAH,
                    ingredients = listOf(
                        Ingredient("1", "Nargile", "💨", true),
                        Ingredient("2", "Elmas Aroma", "💎", true),
                        Ingredient("3", "Kömür", "🔥"),
                        Ingredient("4", "Su", "💧")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "aroma",
                            name = "Aroma Seçimi",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("diamond", "Elmas", "₺0", true),
                                CustomizationOption("apple", "Elma", "₺10"),
                                CustomizationOption("mint", "Nane", "₺10")
                            )
                        ),
                        CustomizationGroup(
                            id = "coal",
                            name = "Kömür Tipi",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("natural", "Doğal Kömür", "₺0", true),
                                CustomizationOption("quick_light", "Hızlı Yakan", "₺15")
                            )
                        )
                    )
                ),
                FoodItem(
                    id = "hookah2",
                    name = "Aromalı Nargile",
                    imageUrl = "https://images.unsplash.com/photo-1603052875731-4b8f3e4e4f3b?q=80&w=400&fit=crop",
                    price = "₺120",
                    info = "Aromalı nargile, meyve tabakları ile",
                    category = "Nargile",
                    rating = 4.7f,
                    ratingCount = 85,
                    foodType = FoodType.HOOKAH,
                    ingredients = listOf(
                        Ingredient("1", "Nargile", "💨", true),
                        Ingredient("2", "Meyve Aroması", "🍎", true),
                        Ingredient("3", "Meyve Tabağı", "🍎"),
                        Ingredient("4", "Kömür", "🔥")
                    )
                )
            ),
            "Atıştırmalıklar" to listOf(
                FoodItem(
                    id = "snack1",
                    name = "Patates Kızartması",
                    imageUrl = "https://images.unsplash.com/photo-1576107232684-1279f390859f?q=80&w=400&fit=crop",
                    price = "₺45",
                    info = "Taze patates, özel sos",
                    category = "Atıştırmalıklar",
                    rating = 4.5f,
                    ratingCount = 234,
                    foodType = FoodType.SNACK,
                    ingredients = listOf(
                        Ingredient("1", "Patates", "🥔", true),
                        Ingredient("2", "Yağ", "🫒", true),
                        Ingredient("3", "Tuz", "🧂"),
                        Ingredient("4", "Baharat", "🌶️")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "size",
                            name = "Porsiyon",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("s", "Küçük", "₺0", true),
                                CustomizationOption("l", "Büyük", "₺15")
                            )
                        ),
                        CustomizationGroup(
                            id = "sauce",
                            name = "Sos",
                            type = CustomizationType.MULTI_SELECT,
                            options = listOf(
                                CustomizationOption("ketchup", "Ketçap", "₺0", true),
                                CustomizationOption("mayo", "Mayonez", "₺0"),
                                CustomizationOption("bbq", "BBQ", "₺5"),
                                CustomizationOption("ranch", "Ranch", "₺5")
                            )
                        )
                    )
                ),
                FoodItem(
                    id = "snack2",
                    name = "Soğan Halkaları",
                    imageUrl = "https://images.unsplash.com/photo-1639024471283-03518883512d?q=80&w=400&fit=crop",
                    price = "₺50",
                    info = "Çıtır soğan halkaları, acı sos",
                    category = "Atıştırmalıklar",
                    rating = 4.4f,
                    ratingCount = 123,
                    foodType = FoodType.SNACK,
                    ingredients = listOf(
                        Ingredient("1", "Soğan", "🧅", true),
                        Ingredient("2", "Un", "🌾", true),
                        Ingredient("3", "Yağ", "🫒"),
                        Ingredient("4", "Baharat", "🌶️")
                    )
                )
            ),
            "Yan Ürünler" to listOf(
                FoodItem(
                    id = "side1",
                    name = "Patates Kızartması",
                    imageUrl = "https://images.unsplash.com/photo-1576107232684-1279f390859f?q=80&w=400&fit=crop",
                    price = "₺45",
                    info = "Taze patates, özel sos",
                    category = "Yan Ürünler",
                    rating = 4.5f,
                    ratingCount = 234,
                    foodType = FoodType.SIDE,
                    ingredients = listOf(
                        Ingredient("1", "Patates", "🥔", true),
                        Ingredient("2", "Yağ", "🫒", true),
                        Ingredient("3", "Tuz", "🧂"),
                        Ingredient("4", "Baharat", "🌶️")
                    ),
                    customizationGroups = listOf(
                        CustomizationGroup(
                            id = "size",
                            name = "Porsiyon",
                            type = CustomizationType.SINGLE_SELECT,
                            isRequired = true,
                            options = listOf(
                                CustomizationOption("s", "Küçük", "₺0", true),
                                CustomizationOption("l", "Büyük", "₺15")
                            )
                        ),
                        CustomizationGroup(
                            id = "sauce",
                            name = "Sos",
                            type = CustomizationType.MULTI_SELECT,
                            options = listOf(
                                CustomizationOption("ketchup", "Ketçap", "₺0", true),
                                CustomizationOption("mayo", "Mayonez", "₺0"),
                                CustomizationOption("bbq", "BBQ", "₺5"),
                                CustomizationOption("ranch", "Ranch", "₺5")
                            )
                        )
                    )
                ),
                FoodItem(
                    id = "side2",
                    name = "Soğan Halkaları",
                    imageUrl = "https://images.unsplash.com/photo-1639024471283-03518883512d?q=80&w=400&fit=crop",
                    price = "₺50",
                    info = "Çıtır soğan halkaları, acı sos",
                    category = "Yan Ürünler",
                    rating = 4.4f,
                    ratingCount = 123,
                    foodType = FoodType.SIDE,
                    ingredients = listOf(
                        Ingredient("1", "Soğan", "🧅", true),
                        Ingredient("2", "Un", "🌾", true),
                        Ingredient("3", "Yağ", "🫒"),
                        Ingredient("4", "Baharat", "🌶️")
                    )
                )
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
                            items(menuCategories) { category ->
                                val isSelected = selectedCategory.value == category
                                Card(
                                    onClick = {
                                        selectedCategory.value = category
                                        categoryIndices[category]?.let { index ->
                                            coroutineScope.launch { lazyListState.animateScrollToItem(index) }
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
                                    quantity = cartItems[item.name] ?: 0,
                                    onClick = {
                                        navController.navigate(FoodDetail.fromFoodItem(item))
                                    },
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
