package com.bekircaglar.qarko.data.repository

import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.domain.repository.IFoodRepository

class FoodRepository() : IFoodRepository {


    // Additional methods for repository can be added here
    override suspend fun getFoodDetails(foodId: String): FoodItem? {
        TODO("Not yet implemented")
    }

    override suspend fun addToCart(
        foodItem: FoodItem,
        quantity: Int,
        selectedOptions: Map<String, String>
    ) {
        TODO("Not yet implemented")
    }


    override suspend fun getCartItems(): List<FoodItem> {
        TODO("Not yet implemented")
    }

    override suspend fun clearCart() {
        TODO("Not yet implemented")
    }
}