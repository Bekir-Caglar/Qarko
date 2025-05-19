package com.bekircaglar.qarko.presentation.food_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.domain.usecase.food.AddToCartUseCase
import com.bekircaglar.qarko.domain.usecase.food.GetFoodDetailsUseCase
import kotlinx.coroutines.launch

class FoodDetailViewModel(
    private val getFoodDetailsUseCase: GetFoodDetailsUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    var foodDetails: FoodItem? = null
        private set

    fun fetchFoodDetails(foodId: String) {
        viewModelScope.launch {
            foodDetails = getFoodDetailsUseCase(foodId)
        }
    }

    fun addToCart(food: FoodItem, quantity: Int, selectedOptions: Map<String, String>) {
        viewModelScope.launch {
            addToCartUseCase(food, quantity, selectedOptions)
        }
    }
}