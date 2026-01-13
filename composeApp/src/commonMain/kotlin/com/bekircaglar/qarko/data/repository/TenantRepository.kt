package com.bekircaglar.qarko.data.repository

import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.data.model.MenuCategory
import com.bekircaglar.qarko.data.model.MenuItem
import com.bekircaglar.qarko.data.model.Table
import com.bekircaglar.qarko.data.model.Tenant
import com.bekircaglar.qarko.domain.repository.ITenantRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TenantRepository : ITenantRepository {

    private val firestore = Firebase.firestore
    private val tenantsCollection = firestore.collection("tenants")

    override suspend fun getTenantBySlug(slug: String): Result<Tenant?> {
        return try {
            val snapshot = tenantsCollection.where("slug", equalTo = slug).get()
            if (snapshot.documents.isNotEmpty()) {
                val document = snapshot.documents.first()
                val tenant = document.data<Tenant>()
                Result.success(tenant)
            } else {
                Result.success(getMockTenant(slug)) // Firestore'da yoksa mock dön (Test için)
            }
        } catch (e: Exception) {
            Result.success(getMockTenant(slug)) // Hata alırsak mock dön (Test için)
        }
    }

    override suspend fun getTenantById(tenantId: String): Result<Tenant?> {
        return try {
            val document = tenantsCollection.document(tenantId).get()
            if (document.exists) {
                Result.success(document.data<Tenant>())
            } else {
                Result.success(getMockTenant(tenantId))
            }
        } catch (e: Exception) {
            Result.success(getMockTenant(tenantId))
        }
    }

    override suspend fun getCategories(tenantId: String): Result<List<MenuCategory>> {
        return try {
            Result.success(getMockCategories())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCategoriesFlow(tenantId: String): Flow<List<MenuCategory>> = flow {
        emit(getMockCategories())
    }

    override suspend fun getMenuItems(tenantId: String): Result<List<MenuItem>> {
        return try {
            Result.success(getMockMenuItems())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMenuItemsByCategory(tenantId: String, categoryId: String): Result<List<MenuItem>> {
        return try {
            val allItems = getMockMenuItems()
            Result.success(allItems.filter { it.categoryId == categoryId })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getMenuItemsFlow(tenantId: String): Flow<List<MenuItem>> = flow {
        emit(getMockMenuItems())
    }

    override suspend fun getMenuItem(tenantId: String, itemId: String): Result<MenuItem?> {
        return try {
            Result.success(getMockMenuItems().find { it.id == itemId })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTables(tenantId: String): Result<List<Table>> {
        return try {
            Result.success(getMockTables())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTable(tenantId: String, tableId: String): Result<Table?> {
        return try {
            Result.success(getMockTables().find { it.id == tableId })
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

    // ==================== MOCK DATA ====================

    private fun getMockTenant(slugOrId: String): Tenant {
        return Tenant(
            id = "tenant_beko_yeri",
            name = "Beko'nun Yeri",
            slug = "beko-yeri",
            logo = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=200",
            coverImage = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800",
            description = "En lezzetli burgerler ve pizzalar",
            isOpen = true
        )
    }

    private fun getMockCategories(): List<MenuCategory> {
        return listOf(
            MenuCategory("cat_favorites", "Favoriler", "⭐", 0, true, 5, "En sevilen ürünler"),
            MenuCategory("cat_starters", "Başlangıçlar", "🥗", 1, true, 8, ""),
            MenuCategory("cat_pizzas", "Pizzalar", "🍕", 2, true, 12, ""),
            MenuCategory("cat_burgers", "Burgerler", "🍔", 3, true, 10, ""),
            MenuCategory("cat_kebabs", "Kebaplar", "🍖", 4, true, 15, ""),
            MenuCategory("cat_mains", "Ana Yemekler", "🍽️", 5, true, 20, ""),
            MenuCategory("cat_soups", "Çorbalar", "🍜", 6, true, 6, ""),
            MenuCategory("cat_pasta", "Makarnalar", "🍝", 7, true, 8, ""),
            MenuCategory("cat_sandwiches", "Sandviçler", "🥪", 8, true, 5, ""),
            MenuCategory("cat_wraps", "Dürümler", "🌯", 9, true, 7, ""),
            MenuCategory("cat_seafood", "Deniz Ürünleri", "🦐", 10, true, 10, ""),
            MenuCategory("cat_salads", "Salatalar", "🥗", 11, true, 6, ""),
            MenuCategory("cat_breakfast", "Kahvaltı", "🍳", 12, true, 12, ""),
            MenuCategory("cat_drinks", "İçecekler", "🥤", 13, true, 15, ""),
            MenuCategory("cat_hot_drinks", "Sıcak İçecekler", "☕", 14, true, 10, ""),
            MenuCategory("cat_alcohol", "Alkollü İçecekler", "🍺", 15, true, 20, ""),
            MenuCategory("cat_desserts", "Tatlılar", "🍰", 16, true, 8, ""),
            MenuCategory("cat_ice_cream", "Dondurma", "🍦", 17, true, 6, ""),
            MenuCategory("cat_waffle", "Waffle", "🧇", 18, true, 4, ""),
            MenuCategory("cat_hookah", "Nargile", "💨", 19, true, 10, ""),
            MenuCategory("cat_snacks", "Atıştırmalıklar", "🍿", 20, true, 8, ""),
            MenuCategory("cat_sides", "Yan Ürünler", "🍟", 21, true, 6, "")
        )
    }

    private fun getMockMenuItems(): List<MenuItem> {
        return listOf(
            // Favoriler
            MenuItem(
                id = "item_1",
                categoryId = "cat_favorites",
                name = "Margherita Pizza",
                description = "Mozzarella, domates, fesleğen",
                price = 120.0,
                imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400",
                foodType = "PIZZA",
                isNew = true,
                prepTime = "25-30",
                calories = 780,
                rating = 4.8f,
                ratingCount = 234
            ),
            MenuItem(
                id = "item_2",
                categoryId = "cat_favorites",
                name = "Cheeseburger",
                description = "Dana eti, cheddar, turşu",
                price = 95.0,
                discountedPrice = 76.0,
                imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?w=400",
                foodType = "BURGER",
                prepTime = "15-20",
                calories = 620,
                rating = 4.6f,
                ratingCount = 189
            ),
            // Pizzalar
            MenuItem(
                id = "item_3",
                categoryId = "cat_pizzas",
                name = "Pepperoni Pizza",
                description = "Pepperoni, mozzarella, domates sosu",
                price = 140.0,
                imageUrl = "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=400",
                foodType = "PIZZA",
                prepTime = "25-30",
                calories = 850,
                rating = 4.7f,
                ratingCount = 156
            ),
            MenuItem(
                id = "item_4",
                categoryId = "cat_pizzas",
                name = "Vejeteryan Pizza",
                description = "Mantar, biber, mısır, zeytin",
                price = 130.0,
                imageUrl = "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400",
                foodType = "PIZZA",
                prepTime = "25-30",
                calories = 650,
                rating = 4.5f,
                ratingCount = 98
            ),
            // Burgerler
            MenuItem(
                id = "item_5",
                categoryId = "cat_burgers",
                name = "Double Burger",
                description = "2 kat dana eti, özel sos",
                price = 135.0,
                imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400",
                foodType = "BURGER",
                prepTime = "20-25",
                calories = 920,
                rating = 4.8f,
                ratingCount = 267
            ),
            MenuItem(
                id = "item_6",
                categoryId = "cat_burgers",
                name = "Chicken Burger",
                description = "Tavuk göğsü, marul, maynoze",
                price = 85.0,
                imageUrl = "https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=400",
                foodType = "BURGER",
                prepTime = "15-20",
                calories = 550,
                rating = 4.4f,
                ratingCount = 123
            ),
            // İçecekler
            MenuItem(
                id = "item_7",
                categoryId = "cat_drinks",
                name = "Coca Cola",
                description = "330ml",
                price = 25.0,
                imageUrl = "https://images.unsplash.com/photo-1554866585-cd94860890b7?w=400",
                foodType = "DRINK",
                prepTime = "1-2",
                calories = 140,
                rating = 4.9f,
                ratingCount = 567
            ),
            MenuItem(
                id = "item_8",
                categoryId = "cat_drinks",
                name = "Ayran",
                description = "Ev yapımı, 300ml",
                price = 15.0,
                imageUrl = "https://images.unsplash.com/photo-1623848932583-b616e50a82a1?w=400",
                foodType = "DRINK",
                prepTime = "1-2",
                calories = 80,
                rating = 4.7f,
                ratingCount = 345
            ),
            // Tatlılar
            MenuItem(
                id = "item_9",
                categoryId = "cat_desserts",
                name = "Çikolatalı Sufle",
                description = "Sıcak çikolata akışı ile",
                price = 75.0,
                imageUrl = "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=400",
                foodType = "DESSERT",
                prepTime = "15-20",
                calories = 450,
                rating = 4.9f,
                ratingCount = 189
            ),
            MenuItem(
                id = "item_10",
                categoryId = "cat_desserts",
                name = "San Sebastian Cheesecake",
                description = "Özel tarif, karamelize yüzey",
                price = 85.0,
                imageUrl = "https://images.unsplash.com/photo-1533134242443-d4fd215305ad?w=400",
                foodType = "DESSERT",
                prepTime = "5-10",
                calories = 380,
                rating = 4.8f,
                ratingCount = 234
            )
        )
    }

    private fun getMockTables(): List<Table> {
        return listOf(
            Table("table_1", "Masa 1", "Salon", 4),
            Table("table_2", "Masa 2", "Salon", 4),
            Table("table_3", "Masa 3", "Salon", 6),
            Table("table_4", "Bahçe 1", "Bahçe", 4),
            Table("table_5", "Bahçe 2", "Bahçe", 6),
            Table("table_6", "Teras 1", "Teras", 2)
        )
    }
}