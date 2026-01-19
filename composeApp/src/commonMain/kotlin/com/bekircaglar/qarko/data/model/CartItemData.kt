package com.bekircaglar.qarko.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItemData(
    val id: String = "", // Unique ID for cart item
    val foodId: String = "", // Original food item ID
    val categoryId: String = "", // Category ID of the food item
    val imageUrl: String = "",
    val name: String = "",
    val description: String = "",
    val basePrice: Double = 0.0,
    val price: Double = 0.0, // Total price including extras
    val quantity: Int = 1,
    val selectedOptions: Map<String, String> = emptyMap(), // groupId -> optionId for single select
    val selectedMultiOptions: Map<String, Set<String>> = emptyMap(), // groupId -> set of optionIds for multi select
    val removedItems: Set<String> = emptySet(), // IDs of removed items
    val note: String = "" // Special note for this item
)
