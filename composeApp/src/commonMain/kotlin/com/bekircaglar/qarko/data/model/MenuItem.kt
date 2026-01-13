package com.bekircaglar.qarko.data.model

import kotlinx.serialization.Serializable

/**
 * Firebase'den alınan MenuItem modeli
 * Firebase path: tenants/{tenantId}/menuItems/{itemId}
 * Bu model Firebase'deki yapıyla 1:1 uyumlu
 */
@Serializable
data class MenuItem(
    val id: String = "",
    val categoryId: String = "", // Bağlı olduğu kategori ID'si
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val discountedPrice: Double? = null,
    val imageUrl: String = "",
    val foodType: String = "OTHER", // FoodType enum string değeri
    val ingredients: List<MenuItemIngredient> = emptyList(),
    val allergens: List<String> = emptyList(), // Alerjen string listesi
    val nutritionInfo: NutritionInfo? = null,
    val customizationGroups: List<MenuItemCustomizationGroup> = emptyList(),
    val isAvailable: Boolean = true,
    val isActive: Boolean = true,
    val isNew: Boolean = false,
    val prepTime: String = "15-20",
    val calories: Int = 0,
    val rating: Float = 0f,
    val ratingCount: Int = 0,
    val sortOrder: Int = 0
) {
    /**
     * MenuItem'ı uygulama içinde kullanılan FoodItem'a dönüştürür
     */
    fun toFoodItem(): FoodItem {
        val foodTypeEnum = try {
            FoodType.valueOf(foodType)
        } catch (e: Exception) {
            FoodType.OTHER
        }

        val allergensList = allergens.mapNotNull { allergenString ->
            try {
                Allergen.valueOf(allergenString)
            } catch (e: Exception) {
                null
            }
        }

        val ingredientsList = ingredients.map {
            Ingredient(
                id = it.id,
                name = it.name,
                iconName = it.iconName,
                isMain = it.isMain
            )
        }

        val customizationGroupsList = customizationGroups.map { group ->
            CustomizationGroup(
                id = group.id,
                name = group.name,
                type = try { CustomizationType.valueOf(group.type) } catch (e: Exception) { CustomizationType.SINGLE_SELECT },
                isRequired = group.isRequired,
                options = group.options.map { option ->
                    CustomizationOption(
                        id = option.id,
                        name = option.name,
                        extraPrice = "₺${option.extraPrice.toInt()}",
                        isDefault = option.isDefault
                    )
                }
            )
        }

        val discountPercent = if (discountedPrice != null && discountedPrice > 0 && price > 0) {
            ((price - discountedPrice) / price * 100).toInt()
        } else 0

        val displayPrice = if (discountedPrice != null && discountedPrice > 0) {
            "₺${discountedPrice.toInt()}"
        } else {
            "₺${price.toInt()}"
        }

        return FoodItem(
            id = id,
            name = name,
            imageUrl = imageUrl,
            price = displayPrice,
            info = description,
            category = categoryId,
            rating = rating,
            ratingCount = ratingCount,
            ingredients = ingredientsList,
            allergens = allergensList,
            customizationGroups = customizationGroupsList,
            removableItems = emptyList(), // Bu bilgi Firebase'de tutulacaksa eklenebilir
            foodType = foodTypeEnum,
            isNew = isNew,
            discountPercent = discountPercent,
            prepTime = prepTime,
            calories = calories
        )
    }
}

/**
 * Firebase MenuItem içerik bilgisi
 */
@Serializable
data class MenuItemIngredient(
    val id: String = "",
    val name: String = "",
    val iconName: String = "", // Emoji veya icon adı
    val isMain: Boolean = false
)

/**
 * Besin değerleri
 */
@Serializable
data class NutritionInfo(
    val calories: Int = 0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val fiber: Double = 0.0,
    val sodium: Double = 0.0
)

/**
 * Firebase MenuItem özelleştirme grubu
 */
@Serializable
data class MenuItemCustomizationGroup(
    val id: String = "",
    val name: String = "",
    val type: String = "SINGLE_SELECT", // CustomizationType string değeri
    val isRequired: Boolean = false,
    val options: List<MenuItemCustomizationOption> = emptyList()
)

/**
 * Firebase MenuItem özelleştirme seçeneği
 */
@Serializable
data class MenuItemCustomizationOption(
    val id: String = "",
    val name: String = "",
    val extraPrice: Double = 0.0,
    val isDefault: Boolean = false
)

