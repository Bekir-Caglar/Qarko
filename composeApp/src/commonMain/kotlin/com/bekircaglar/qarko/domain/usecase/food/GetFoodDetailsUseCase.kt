package com.bekircaglar.qarko.domain.usecase.food

import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.domain.repository.IFoodRepository

class GetFoodDetailsUseCase(private val foodRepository: IFoodRepository) {
    suspend operator fun invoke(foodId: String): FoodItem? {
        return foodRepository.getFoodDetails(foodId)
    }
}