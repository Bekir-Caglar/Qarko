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

/**
 * TenantMenu ekranı için ViewModel
 * Tenant'ın menü, kategori ve işletme bilgilerini yönetir
 */
class TenantMenuViewModel(
    private val loadTenantFromQRUseCase: LoadTenantFromQRUseCase,
    private val getTenantMenuUseCase: GetTenantMenuUseCase
) : ViewModel() {

    // UI State
    var uiState by mutableStateOf(TenantMenuUiState())
        private set

    // Aktif tenant bilgisi (TenantSession'dan)
    val currentTenant: Tenant?
        get() = TenantSession.currentTenant

    val categories: List<MenuCategory>
        get() = TenantSession.categories

    val tableId: String?
        get() = TenantSession.tableId

    val tableName: String?
        get() = TenantSession.currentTable?.name

    init {
        // Eğer session aktifse menüyü yükle
        if (TenantSession.isSessionActive) {
            loadMenu()
        }
    }

    /**
     * QR kod URL'inden tenant'ı yükler
     */
    fun loadTenantFromQR(qrUrl: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            val result = loadTenantFromQRUseCase(qrUrl)

            result.onSuccess { loadResult ->
                // Menüyü de yükle
                loadMenu()
            }.onFailure { exception ->
                uiState = uiState.copy(
                    isLoading = false,
                    error = exception.message ?: "Bir hata oluştu"
                )
            }
        }
    }

    /**
     * Direkt tenant slug ve table id ile yükleme
     */
    fun loadTenant(tenantSlug: String, tableId: String? = null) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            val result = loadTenantFromQRUseCase.loadDirect(tenantSlug, tableId)

            result.onSuccess {
                loadMenu()
            }.onFailure { exception ->
                uiState = uiState.copy(
                    isLoading = false,
                    error = exception.message ?: "Bir hata oluştu"
                )
            }
        }
    }

    /**
     * Aktif tenant'ın menüsünü yükler
     */
    fun loadMenu() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            val result = getTenantMenuUseCase()

            result.onSuccess { menuData ->
                uiState = uiState.copy(
                    isLoading = false,
                    categories = menuData.categories,
                    allItems = menuData.allItems,
                    categorizedItems = menuData.categorizedItems,
                    error = null
                )
            }.onFailure { exception ->
                uiState = uiState.copy(
                    isLoading = false,
                    error = exception.message ?: "Menü yüklenirken hata oluştu"
                )
            }
        }
    }

    /**
     * Belirli bir kategorinin ürünlerini getirir
     */
    fun getItemsForCategory(categoryId: String): List<FoodItem> {
        return uiState.allItems.filter { it.category == categoryId }
    }

    /**
     * Session'ı temizle (çıkış yaparken)
     */
    fun clearSession() {
        TenantSession.clearSession()
        uiState = TenantMenuUiState()
    }

    /**
     * Hatayı temizle
     */
    fun clearError() {
        uiState = uiState.copy(error = null)
    }
}

/**
 * TenantMenu UI State
 */
data class TenantMenuUiState(
    val isLoading: Boolean = false,
    val categories: List<MenuCategory> = emptyList(),
    val allItems: List<FoodItem> = emptyList(),
    val categorizedItems: Map<MenuCategory, List<FoodItem>> = emptyMap(),
    val error: String? = null
)
