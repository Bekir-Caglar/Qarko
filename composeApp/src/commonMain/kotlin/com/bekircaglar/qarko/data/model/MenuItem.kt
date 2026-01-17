package com.bekircaglar.qarko.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MenuItem(
    val id: String = "",
    val categoryId: String = "",
    val name: String = "",
    val description: String = "",
    val image: String = "",
    val imageUrl: String = "",
    val price: Double = 0.0,
    val originalPrice: Double = 0.0,
    val discountedPrice: Double? = null,
    val foodType: String? = null,
    val ingredients: List<MenuItemIngredient> = emptyList(),
    val removableItems: List<MenuItemRemovableItem> = emptyList(),
    val allergens: List<String> = emptyList(),
    val customizationGroups: List<MenuItemCustomizationGroup> = emptyList(),
    val isActive: Boolean = true,
    val isAvailable: Boolean = true,
    val isFeatured: Boolean = false,
    val isNew: Boolean = false,
    val preparationTime: Int? = null,
    val calories: Int = 0,
    val sortOrder: Int = 0,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val suggestedPairingCategoryIds: List<String> = emptyList()
) {
    fun toFoodItem(): FoodItem {
        val allergensList = allergens.mapNotNull { try { Allergen.valueOf(it) } catch (e: Exception) { null } }
        val ingredientsList = ingredients.map { Ingredient(id = it.id, name = it.name, iconName = it.emoji, isMain = it.isMain) }
        val removableList = removableItems.map { RemovableItem(id = it.id, name = it.name, emoji = it.emoji) }
        val customizationGroupsList = customizationGroups.map { group ->
            CustomizationGroup(
                id = group.id,
                name = group.name,
                type = try { CustomizationType.valueOf(group.type) } catch (e: Exception) { CustomizationType.SINGLE_SELECT },
                isRequired = group.isRequired,
                options = group.options.map { option ->
                    CustomizationOption(id = option.id, name = option.name, extraPrice = "₺${option.price.toInt()}", isDefault = option.isDefault)
                }
            )
        }

        val hasDiscount = originalPrice > price && price > 0
        
        val discountPercent = if (hasDiscount && originalPrice > 0) {
            (((originalPrice - price) / originalPrice) * 100).toInt()
        } else 0

        return FoodItem(
            id = id,
            name = name,
            imageUrl = if (imageUrl.isNotBlank()) imageUrl else image,
            price = "₺${price.toInt()}",
            originalPrice = if (hasDiscount) "₺${originalPrice.toInt()}" else null,
            info = description,
            category = categoryId,
            ingredients = ingredientsList,
            allergens = allergensList,
            customizationGroups = customizationGroupsList,
            removableItems = removableList,
            isNew = isNew,
            isFeatured = isFeatured,
            discountPercent = discountPercent,
            prepTime = preparationTime?.toString(),
            calories = calories,
            suggestedPairingCategoryIds = suggestedPairingCategoryIds
        )
    }
}

@Serializable data class MenuItemIngredient(val id: String = "", val name: String = "", val emoji: String = "", val isMain: Boolean = false)
@Serializable data class MenuItemRemovableItem(val id: String = "", val name: String = "", val emoji: String = "")
@Serializable data class MenuItemCustomizationGroup(val id: String = "", val name: String = "", val type: String = "SINGLE_SELECT", val isRequired: Boolean = false, val options: List<MenuItemCustomizationOption> = emptyList())
@Serializable data class MenuItemCustomizationOption(val id: String = "", val name: String = "", val price: Double = 0.0, val isDefault: Boolean = false)
