package com.bekircaglar.qarko.domain.repository

import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.data.model.MenuCategory
import com.bekircaglar.qarko.data.model.MenuItem
import com.bekircaglar.qarko.data.model.Table
import com.bekircaglar.qarko.data.model.Tenant
import kotlinx.coroutines.flow.Flow

/**
 * Tenant (işletme) verilerine erişim için repository interface
 */
interface ITenantRepository {

    /**
     * Slug'a göre tenant'ı getirir
     * @param slug Tenant slug (örn: beko-yeri)
     */
    suspend fun getTenantBySlug(slug: String): Result<Tenant?>

    /**
     * ID'ye göre tenant'ı getirir
     * @param tenantId Tenant ID
     */
    suspend fun getTenantById(tenantId: String): Result<Tenant?>

    /**
     * Tenant'ın kategorilerini getirir
     * @param tenantId Tenant ID
     */
    suspend fun getCategories(tenantId: String): Result<List<MenuCategory>>

    /**
     * Tenant'ın kategorilerini Flow olarak getirir (real-time)
     * @param tenantId Tenant ID
     */
    fun getCategoriesFlow(tenantId: String): Flow<List<MenuCategory>>

    /**
     * Tenant'ın menü itemlarını getirir
     * @param tenantId Tenant ID
     */
    suspend fun getMenuItems(tenantId: String): Result<List<MenuItem>>

    /**
     * Kategoriye göre menü itemları getirir
     * @param tenantId Tenant ID
     * @param categoryId Kategori ID
     */
    suspend fun getMenuItemsByCategory(tenantId: String, categoryId: String): Result<List<MenuItem>>

    /**
     * Menü itemlarını Flow olarak getirir (real-time)
     * @param tenantId Tenant ID
     */
    fun getMenuItemsFlow(tenantId: String): Flow<List<MenuItem>>

    /**
     * Tek bir menü item'ı getirir
     * @param tenantId Tenant ID
     * @param itemId Item ID
     */
    suspend fun getMenuItem(tenantId: String, itemId: String): Result<MenuItem?>

    /**
     * Tenant'ın masalarını getirir
     * @param tenantId Tenant ID
     */
    suspend fun getTables(tenantId: String): Result<List<Table>>

    /**
     * ID'ye göre masa getirir
     * @param tenantId Tenant ID
     * @param tableId Table ID
     */
    suspend fun getTable(tenantId: String, tableId: String): Result<Table?>

    /**
     * Tenant'ın menüsünü FoodItem listesi olarak getirir (dönüştürülmüş)
     * @param tenantId Tenant ID
     */
    suspend fun getFoodItems(tenantId: String): Result<List<FoodItem>>

    /**
     * Kategoriye göre FoodItem listesi getirir (dönüştürülmüş)
     * @param tenantId Tenant ID
     * @param categoryId Kategori ID
     */
    suspend fun getFoodItemsByCategory(tenantId: String, categoryId: String): Result<List<FoodItem>>
}

