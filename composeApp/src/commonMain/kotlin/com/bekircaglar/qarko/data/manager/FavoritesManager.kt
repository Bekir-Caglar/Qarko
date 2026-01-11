package com.bekircaglar.qarko.data.manager

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.bekircaglar.qarko.data.model.FoodItem

/**
 * Global Favorites Manager - Singleton pattern for managing favorites state across the app
 * Her işletme (tenant) için ayrı favori listesi tutar
 */
object FavoritesManager {

    // tenantId -> Map<foodId, FoodItem>
    private val _favoritesByTenant = mutableStateMapOf<String, SnapshotStateMap<String, FoodItem>>()

    // Şu anki aktif tenant ID (QR tarandığında set edilecek)
    private var _currentTenantId: String = "default_tenant"

    val currentTenantId: String
        get() = _currentTenantId

    /**
     * Tenant ID'yi set et (QR tarandığında çağrılacak)
     */
    fun setCurrentTenant(tenantId: String) {
        _currentTenantId = tenantId
        // Tenant için favori map'i yoksa oluştur
        if (!_favoritesByTenant.containsKey(tenantId)) {
            _favoritesByTenant[tenantId] = mutableStateMapOf()
        }
    }

    /**
     * Mevcut tenant'ın favorilerini getir
     */
    fun getCurrentTenantFavorites(): List<FoodItem> {
        return _favoritesByTenant[_currentTenantId]?.values?.toList() ?: emptyList()
    }

    /**
     * Belirli bir tenant'ın favorilerini getir
     */
    fun getFavorites(tenantId: String): List<FoodItem> {
        return _favoritesByTenant[tenantId]?.values?.toList() ?: emptyList()
    }

    /**
     * Ürün favorilere ekle
     */
    fun addToFavorites(foodItem: FoodItem, tenantId: String = _currentTenantId) {
        if (!_favoritesByTenant.containsKey(tenantId)) {
            _favoritesByTenant[tenantId] = mutableStateMapOf()
        }
        _favoritesByTenant[tenantId]?.put(foodItem.id, foodItem)
    }

    /**
     * Ürün favorilerden çıkar
     */
    fun removeFromFavorites(foodId: String, tenantId: String = _currentTenantId) {
        _favoritesByTenant[tenantId]?.remove(foodId)
    }

    /**
     * Favori durumunu toggle et
     */
    fun toggleFavorite(foodItem: FoodItem, tenantId: String = _currentTenantId): Boolean {
        return if (isFavorite(foodItem.id, tenantId)) {
            removeFromFavorites(foodItem.id, tenantId)
            false
        } else {
            addToFavorites(foodItem, tenantId)
            true
        }
    }

    /**
     * Ürün favorilerde mi kontrol et
     */
    fun isFavorite(foodId: String, tenantId: String = _currentTenantId): Boolean {
        return _favoritesByTenant[tenantId]?.containsKey(foodId) ?: false
    }

    /**
     * Mevcut tenant'ın favori sayısı
     */
    fun getFavoritesCount(tenantId: String = _currentTenantId): Int {
        return _favoritesByTenant[tenantId]?.size ?: 0
    }

    /**
     * Tüm tenant'ların toplam favori sayısı
     */
    fun getTotalFavoritesCount(): Int {
        return _favoritesByTenant.values.sumOf { it.size }
    }

    /**
     * Mevcut tenant'ın favorilerini temizle
     */
    fun clearCurrentTenantFavorites() {
        _favoritesByTenant[_currentTenantId]?.clear()
    }

    /**
     * Tüm favorileri temizle
     */
    fun clearAllFavorites() {
        _favoritesByTenant.clear()
    }
}

