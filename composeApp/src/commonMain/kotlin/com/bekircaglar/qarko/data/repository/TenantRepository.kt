package com.bekircaglar.qarko.data.repository

import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.data.model.MenuCategory
import com.bekircaglar.qarko.data.model.MenuItem
import com.bekircaglar.qarko.data.model.Table
import com.bekircaglar.qarko.data.model.Tenant
import com.bekircaglar.qarko.domain.repository.ITenantRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TenantRepository : ITenantRepository {

    private val firestore = Firebase.firestore
    private val tenantsCollection = firestore.collection("tenants")

    override suspend fun getTenantBySlug(slug: String): Result<Tenant?> {
        val trimmedSlug = slug.trim()
        if (trimmedSlug.isEmpty()) return Result.failure(Exception("Slug boş olamaz"))

        return try {
            // 1. STRATEJİ: Slug'ın doğrudan Döküman ID'si olduğunu varsayalım (En hızlı yöntem)
            // Örn: tenants/beko-yeri
            val docRef = tenantsCollection.document(trimmedSlug).get()

            if (docRef.exists) {
                try {
                    val tenantData = docRef.data<Tenant>()
                    // ID mapping: Eğer data içindeki id boşsa doküman id'sini kullan
                    val tenant = if (tenantData.id.isEmpty()) {
                        tenantData.copy(id = docRef.id)
                    } else {
                        tenantData
                    }
                    return Result.success(tenant)
                } catch (e: Exception) {
                    println("Tenant data mapping hatası (ID ile): ${e.message}")
                    // Mapping hatası varsa aşağıda query ile şansımızı tekrar deneyebiliriz
                }
            }

            // 2. STRATEJİ: Slug bir alan (field) olabilir, sorgu atalım
            val snapshot = tenantsCollection.where("slug", equalTo = trimmedSlug).get()

            if (snapshot.documents.isNotEmpty()) {
                val doc = snapshot.documents.first()
                val tenantData = doc.data<Tenant>()
                val tenant = if (tenantData.id.isEmpty()) {
                    tenantData.copy(id = doc.id)
                } else {
                    tenantData
                }
                Result.success(tenant)
            } else {
                Result.failure(Exception("Bu slug ile bir işletme bulunamadı: $trimmedSlug"))
            }

        } catch (e: Exception) {
            // Permission Denied hataları burada yakalanır
            // Eğer "Missing or insufficient permissions" hatası alıyorsanız, Firestore Rules'da
            // 'tenants' koleksiyonunun public okumaya (read) açık olduğundan emin olun.
            Result.failure(e)
        }
    }

    override suspend fun getTenantById(tenantId: String): Result<Tenant?> {
        return try {
            val document = tenantsCollection.document(tenantId).get()
            if (document.exists) {
                val tenantData = document.data<Tenant>()
                val tenant = if (tenantData.id.isEmpty()) {
                    tenantData.copy(id = document.id)
                } else {
                    tenantData
                }
                Result.success(tenant)
            } else {
                Result.failure(Exception("Tenant bulunamadı ID: $tenantId"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategories(tenantId: String): Result<List<MenuCategory>> {
        if (tenantId.isBlank()) return Result.failure(Exception("Tenant ID boş olamaz"))

        return try {
            // path: tenants/{tenantId}/categories
            val snapshot = tenantsCollection
                .document(tenantId)
                .collection("categories")
                .orderBy("sortOrder", Direction.ASCENDING) // Admin panelindeki gibi sıralı getir
                .get()

            val categories = snapshot.documents.map { doc ->
                val data = doc.data<MenuCategory>()
                if (data.id.isEmpty()) data.copy(id = doc.id) else data
            }
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCategoriesFlow(tenantId: String): Flow<List<MenuCategory>> {
        return tenantsCollection
            .document(tenantId)
            .collection("categories")
            .orderBy("sortOrder", Direction.ASCENDING)
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    val data = doc.data<MenuCategory>()
                    if (data.id.isEmpty()) data.copy(id = doc.id) else data
                }
            }
    }

    override suspend fun getMenuItems(tenantId: String): Result<List<MenuItem>> {
        return try {
            // path: tenants/{tenantId}/menuItems (Flat structure)
            val snapshot = tenantsCollection
                .document(tenantId)
                .collection("menuItems")
                .get()

            val items = snapshot.documents.map { doc ->
                val data = doc.data<MenuItem>()
                if (data.id.isEmpty()) data.copy(id = doc.id) else data
            }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMenuItemsByCategory(tenantId: String, categoryId: String): Result<List<MenuItem>> {
        return try {
            val snapshot = tenantsCollection
                .document(tenantId)
                .collection("menuItems")
                .where("categoryId", equalTo = categoryId)
                .get()

            val items = snapshot.documents.map { doc ->
                val data = doc.data<MenuItem>()
                if (data.id.isEmpty()) data.copy(id = doc.id) else data
            }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getMenuItemsFlow(tenantId: String): Flow<List<MenuItem>> {
        return tenantsCollection
            .document(tenantId)
            .collection("menuItems")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    val data = doc.data<MenuItem>()
                    if (data.id.isEmpty()) data.copy(id = doc.id) else data
                }
            }
    }

    override suspend fun getMenuItem(tenantId: String, itemId: String): Result<MenuItem?> {
        return try {
            val doc = tenantsCollection
                .document(tenantId)
                .collection("menuItems")
                .document(itemId)
                .get()

            if (doc.exists) {
                val data = doc.data<MenuItem>()
                val item = if (data.id.isEmpty()) data.copy(id = doc.id) else data
                Result.success(item)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTables(tenantId: String): Result<List<Table>> {
        return try {
            val snapshot = tenantsCollection
                .document(tenantId)
                .collection("tables")
                .orderBy("sortOrder", Direction.ASCENDING)
                .get()

            val tables = snapshot.documents.map { doc ->
                val data = doc.data<Table>()
                if (data.id.isEmpty()) data.copy(id = doc.id) else data
            }
            Result.success(tables)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTable(tenantId: String, tableId: String): Result<Table?> {
        return try {
            val doc = tenantsCollection
                .document(tenantId)
                .collection("tables")
                .document(tableId)
                .get()

            if (doc.exists) {
                val data = doc.data<Table>()
                val table = if (data.id.isEmpty()) data.copy(id = doc.id) else data
                Result.success(table)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFoodItems(tenantId: String): Result<List<FoodItem>> {
        return try {
            val menuItemsResult = getMenuItems(tenantId)
            menuItemsResult.map { items ->
                items.map { it.toFoodItem() }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFoodItemsByCategory(tenantId: String, categoryId: String): Result<List<FoodItem>> {
        return try {
            val menuItemsResult = getMenuItemsByCategory(tenantId, categoryId)
            menuItemsResult.map { items ->
                items.map { it.toFoodItem() }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}