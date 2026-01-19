package com.bekircaglar.qarko.presentation.tenant

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.qarko.data.manager.TenantSession
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.data.model.MenuCategory
import com.bekircaglar.qarko.data.model.Tenant
import com.bekircaglar.qarko.domain.usecase.tenant.GetTenantMenuUseCase
import com.bekircaglar.qarko.domain.usecase.tenant.LoadTenantFromQRUseCase
import kotlinx.coroutines.launch

class TenantMenuViewModel(
    private val loadTenantFromQRUseCase: LoadTenantFromQRUseCase,
    private val getTenantMenuUseCase: GetTenantMenuUseCase
) : ViewModel() {

    var uiState by mutableStateOf(TenantMenuUiState())
        private set

    val currentTenant: Tenant?
        get() = TenantSession.currentTenant

    init {
        if (TenantSession.isSessionActive) {
            loadMenu()
        }
    }

    fun loadTenant(tenantSlug: String, tableId: String? = null) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val result = loadTenantFromQRUseCase.loadDirect(tenantSlug, tableId)
            result.onSuccess {
                loadMenu()
            }.onFailure { exception ->
                println("TENANT_LOAD_ERROR: ${exception.message}")
                exception.printStackTrace()
                uiState = uiState.copy(
                    isLoading = false,
                    error = "İşletme yüklenemedi: ${exception.message}"
                )
            }
        }
    }

    fun loadMenu() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val result = getTenantMenuUseCase()
            result.onSuccess { menuData ->
                println("MENU_LOAD_SUCCESS: ${menuData.categories.size} categories, ${menuData.allItems.size} items found.")
                
                // TenantSession'ı güncelle ki diğer ekranlar (Kampanya Seç vb.) bu verilere erişebilsin
                TenantSession.updateCategories(menuData.categories)
                TenantSession.updateMenuItems(menuData.allItems)

                uiState = uiState.copy(
                    isLoading = false,
                    categories = menuData.categories,
                    allItems = menuData.allItems,
                    categorizedItems = menuData.categorizedItems,
                    error = null
                )
            }.onFailure { exception ->
                println("MENU_LOAD_ERROR: ${exception.message}")
                exception.printStackTrace()
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Menü yüklenirken hata oluştu: ${exception.message}"
                )
            }
        }
    }

    fun clearSession() {
        TenantSession.clearSession()
        uiState = TenantMenuUiState()
    }
}

data class TenantMenuUiState(
    val isLoading: Boolean = false,
    val categories: List<MenuCategory> = emptyList(),
    val allItems: List<FoodItem> = emptyList(),
    val categorizedItems: Map<MenuCategory, List<FoodItem>> = emptyMap(),
    val error: String? = null
)
