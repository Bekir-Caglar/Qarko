package com.bekircaglar.qarko.presentation.cart

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.qarko.data.manager.CartManager
import com.bekircaglar.qarko.data.manager.TenantSession
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.domain.repository.ITenantRepository
import kotlinx.coroutines.launch

class CartViewModel(
    private val tenantRepository: ITenantRepository
) : ViewModel() {

    var uiState by mutableStateOf(CartUiState())
        private set

    init {
        loadUpsellRecommendations()
    }

    /**
     * Sepetteki ürünlere göre "Yanında İyi Gider" önerilerini yükler
     */
    fun loadUpsellRecommendations() {
        val tenantId = TenantSession.tenantId ?: return
        val cartItems = CartManager.cartItems
        
        if (cartItems.isEmpty()) {
            uiState = uiState.copy(recommendations = emptyList())
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isRecommendationsLoading = true)
            
            try {
                // 1. Tüm menü ürünlerini ve kategorileri al
                val menuResult = tenantRepository.getFoodItems(tenantId)
                val categoryResult = tenantRepository.getCategories(tenantId)
                
                val allMenuItems = menuResult.getOrDefault(emptyList())
                val allCategories = categoryResult.getOrDefault(emptyList())

                // 2. Sepetteki ürünleri tespit et
                val cartFoodIds = cartItems.map { it.foodId }.toSet()
                val cartFoodItems = allMenuItems.filter { it.id in cartFoodIds }
                
                // 3. Önerilecek kategori ID'lerini topla
                val suggestedCategoryIds = mutableSetOf<String>()
                
                cartFoodItems.forEach { foodItem ->
                    if (foodItem.suggestedPairingCategoryIds.isNotEmpty()) {
                        // Ürün bazlı ayar varsa onu ekle (Override)
                        suggestedCategoryIds.addAll(foodItem.suggestedPairingCategoryIds)
                    } else {
                        // Ürün bazlı yoksa kategori bazlı ayarı bul ve ekle
                        val category = allCategories.find { it.id == foodItem.category }
                        category?.suggestedPairingCategoryIds?.let {
                            suggestedCategoryIds.addAll(it)
                        }
                    }
                }

                if (suggestedCategoryIds.isEmpty()) {
                    uiState = uiState.copy(recommendations = emptyList(), isRecommendationsLoading = false)
                    return@launch
                }

                // 4. Belirlenen kategorilerdeki uygun ürünleri çek
                val recommendations = allMenuItems.filter { 
                    it.category in suggestedCategoryIds && 
                    it.id !in cartFoodIds // Zaten sepette olanı önerme
                }.shuffled().take(5) // Rastgele 5 tane seç

                uiState = uiState.copy(
                    recommendations = recommendations,
                    isRecommendationsLoading = false
                )
            } catch (e: Exception) {
                uiState = uiState.copy(isRecommendationsLoading = false)
            }
        }
    }
}

data class CartUiState(
    val recommendations: List<FoodItem> = emptyList(),
    val isRecommendationsLoading: Boolean = false
)
