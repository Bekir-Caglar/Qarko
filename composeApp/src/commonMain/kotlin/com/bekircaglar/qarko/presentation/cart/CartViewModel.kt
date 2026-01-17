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
                // 1. Tüm menü ürünlerini al (veya kategori bazlı sorgu atılabilir)
                // Performans için tüm menüyü bir kez çekip filtrelemek mantıklı olabilir 
                // ya da direkt kategori listesiyle sorgu atılabilir.
                val menuResult = tenantRepository.getFoodItems(tenantId)
                val allMenuItems = menuResult.getOrDefault(emptyList())

                // 2. Sepetteki ürünlerin önerilen kategori ID'lerini topla
                // Önemli: Sepetteki FoodItem nesnelerine ulaşmamız lazım. 
                // Şimdilik sepette FoodItem'ın kendisi de olabilirdi ama ID üzerinden eşleştireceğiz.
                
                // NOT: CartManager'daki CartItemData'ya FoodItem referansı eklemek gerekebilir 
                // ya da repository'den sepetteki id'lere göre FoodItem'ları tekrar çekmeliyiz.
                // Basitlik adına tüm menü içinden sepettekileri bulup suggestedPairingCategoryIds'leri alıyoruz.
                
                val cartFoodIds = cartItems.map { it.foodId }.toSet()
                val cartFoodItems = allMenuItems.filter { it.id in cartFoodIds }
                
                val suggestedCategoryIds = cartFoodItems
                    .flatMap { it.suggestedPairingCategoryIds }
                    .distinct()
                    .take(10) // Firestore limitine takılmamak için

                if (suggestedCategoryIds.isEmpty()) {
                    uiState = uiState.copy(recommendations = emptyList(), isRecommendationsLoading = false)
                    return@launch
                }

                // 3. Bu kategorilerdeki ürünleri filtrele
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
