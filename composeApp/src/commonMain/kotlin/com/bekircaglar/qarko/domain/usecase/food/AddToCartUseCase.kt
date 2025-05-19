package com.bekircaglar.qarko.domain.usecase.food

import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.domain.repository.IFoodRepository

class AddToCartUseCase(private val foodRepository: IFoodRepository) {
    suspend operator fun invoke(foodItem: FoodItem, quantity: Int, selectedOptions: Map<String, String>) {
        foodRepository.addToCart(foodItem, quantity, selectedOptions)
    }
}