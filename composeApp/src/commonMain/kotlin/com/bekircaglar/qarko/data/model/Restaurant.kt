package com.bekircaglar.qarko.data.model
data class Restaurant(
    val name: String,
    val categories: String,
    val rating: Float,
    val reviewCount: Int,
    val imageUrl: String,
    val deliveryTime: String,
    val distance: String
)