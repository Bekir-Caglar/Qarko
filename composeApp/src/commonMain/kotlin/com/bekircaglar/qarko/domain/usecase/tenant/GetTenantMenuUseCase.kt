package com.bekircaglar.qarko.domain.usecase.tenant

import com.bekircaglar.qarko.data.manager.TenantSession
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.data.model.MenuCategory
import com.bekircaglar.qarko.domain.repository.ITenantRepository

class GetTenantMenuUseCase(
    private val tenantRepository: ITenantRepository
) {
    suspend operator fun invoke(): Result<TenantMenuData> {
        val tenantId = TenantSession.tenantId
            ?: return Result.failure(NoActiveTenantException("Aktif işletme bulunamadı"))
        return getMenu(tenantId)
    }

    suspend fun getMenu(tenantId: String): Result<TenantMenuData> {
        return try {
            println("FETCHING_MENU_FOR_TENANT: $tenantId")
            
            // Hataları yutmamak için getOrThrow kullanıyoruz
            val categories = tenantRepository.getCategories(tenantId).getOrThrow()
            val items = tenantRepository.getFoodItems(tenantId).getOrThrow()

            val categorizedMenu = categories.associateWith { category ->
                items.filter { it.category == category.id }
            }

            Result.success(
                TenantMenuData(
                    categories = categories,
                    allItems = items,
                    categorizedItems = categorizedMenu
                )
            )
        } catch (e: Exception) {
            println("GET_MENU_USE_CASE_EXCEPTION: ${e.message}")
            Result.failure(e)
        }
    }
}

data class TenantMenuData(
    val categories: List<MenuCategory>,
    val allItems: List<FoodItem>,
    val categorizedItems: Map<MenuCategory, List<FoodItem>>
)

class NoActiveTenantException(message: String) : Exception(message)
