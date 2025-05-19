package com.bekircaglar.qarko.domain.repository

import com.bekircaglar.qarko.data.model.FoodItem

interface IFoodRepository {
    suspend fun getFoodDetails(foodId: String): FoodItem?
    suspend fun addToCart(foodItem: FoodItem, quantity: Int, selectedOptions: Map<String, String>)
    suspend fun getCartItems(): List<FoodItem>
    suspend fun clearCart()
}