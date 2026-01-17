package com.bekircaglar.qarko.presentation.food_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.qarko.data.manager.TenantSession
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.domain.repository.ITenantRepository
import com.bekircaglar.qarko.domain.usecase.food.AddToCartUseCase
import com.bekircaglar.qarko.domain.usecase.food.GetFoodDetailsUseCase
import kotlinx.coroutines.launch

class FoodDetailViewModel(
    private val getFoodDetailsUseCase: GetFoodDetailsUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val tenantRepository: ITenantRepository
) : ViewModel() {

    var uiState by mutableStateOf(FoodDetailUiState())
        private set

    /**
     * Navigasyondan gelen FoodItem ile state'i ilklendirir ve önerileri yükler
     */
    fun setFood(food: FoodItem) {
        uiState = uiState.copy(food = food)
        loadRecommendations(food)
    }

    fun fetchFoodDetails(foodId: String) {
        viewModelScope.launch {
            val food = getFoodDetailsUseCase(foodId)
            if (food != null) {
                uiState = uiState.copy(food = food)
                loadRecommendations(food)
            }
        }
    }

    private fun loadRecommendations(food: FoodItem) {
        val tenantId = TenantSession.tenantId ?: return
        if (food.suggestedPairingCategoryIds.isEmpty()) {
            uiState = uiState.copy(recommendations = emptyList())
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isRecommendationsLoading = true)
            try {
                val menuResult = tenantRepository.getFoodItems(tenantId)
                val allMenuItems = menuResult.getOrDefault(emptyList())

                val recommendations = allMenuItems.filter { 
                    it.category in food.suggestedPairingCategoryIds && 
                    it.id != food.id
                }.shuffled().take(5)

                uiState = uiState.copy(
                    recommendations = recommendations,
                    isRecommendationsLoading = false
                )
            } catch (e: Exception) {
                uiState = uiState.copy(isRecommendationsLoading = false)
            }
        }
    }

    fun addToCart(food: FoodItem, quantity: Int, selectedOptions: Map<String, String>) {
        viewModelScope.launch {
            addToCartUseCase(food, quantity, selectedOptions)
        }
    }
}

data class FoodDetailUiState(
    val food: FoodItem? = null,
    val recommendations: List<FoodItem> = emptyList(),
    val isRecommendationsLoading: Boolean = false
)
