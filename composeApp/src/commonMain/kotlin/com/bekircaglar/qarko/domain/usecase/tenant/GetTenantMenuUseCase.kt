package com.bekircaglar.qarko.domain.usecase.tenant

import com.bekircaglar.qarko.data.manager.TenantSession
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.data.model.MenuCategory
import com.bekircaglar.qarko.domain.repository.ITenantRepository

/**
 * Aktif tenant'ın menüsünü getiren use case
 */
class GetTenantMenuUseCase(
    private val tenantRepository: ITenantRepository
) {

    /**
     * Aktif tenant'ın tüm menüsünü kategorilere göre gruplandırılmış getirir
     */
    suspend operator fun invoke(): Result<TenantMenuData> {
        val tenantId = TenantSession.tenantId
            ?: return Result.failure(NoActiveTenantException("Aktif işletme bulunamadı"))

        return getMenu(tenantId)
    }

    /**
     * Belirli bir tenant'ın menüsünü getirir
     */
    suspend fun getMenu(tenantId: String): Result<TenantMenuData> {
        // Kategorileri al
        val categoriesResult = tenantRepository.getCategories(tenantId)
        val categories = categoriesResult.getOrElse { emptyList() }

        // Menü itemlarını al ve FoodItem'a dönüştür
        val itemsResult = tenantRepository.getFoodItems(tenantId)
        val items = itemsResult.getOrElse { emptyList() }

        // Kategorilere göre gruplandır
        val categorizedMenu = categories.associate { category ->
            category to items.filter { it.category == category.id }
        }

        return Result.success(
            TenantMenuData(
                categories = categories,
                allItems = items,
                categorizedItems = categorizedMenu
            )
        )
    }

    /**
     * Belirli bir kategorinin ürünlerini getirir
     */
    suspend fun getItemsByCategory(categoryId: String): Result<List<FoodItem>> {
        val tenantId = TenantSession.tenantId
            ?: return Result.failure(NoActiveTenantException("Aktif işletme bulunamadı"))

        return tenantRepository.getFoodItemsByCategory(tenantId, categoryId)
    }
}

/**
 * Tenant menü verisi
 */
data class TenantMenuData(
    val categories: List<MenuCategory>,
    val allItems: List<FoodItem>,
    val categorizedItems: Map<MenuCategory, List<FoodItem>>
)

/**
 * Aktif tenant yok hatası
 */
class NoActiveTenantException(message: String) : Exception(message)

