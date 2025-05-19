package com.bekircaglar.qarko.data.model


data class FoodItem(
    val name: String,
    val imageUrl: String,
    val price: String,
    val info: String,
    val category: String = "",
    val allergens: List<Allergen> = emptyList()
)
