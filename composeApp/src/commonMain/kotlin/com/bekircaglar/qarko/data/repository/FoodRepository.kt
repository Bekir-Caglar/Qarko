package com.bekircaglar.qarko.data.repository

import com.bekircaglar.qarko.data.manager.TenantSession
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.data.model.MenuItem
import com.bekircaglar.qarko.domain.repository.IFoodRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class FoodRepository() : IFoodRepository {

    private val firestore = Firebase.firestore
    private val tenantsCollection = firestore.collection("tenants")

    override suspend fun getFoodDetails(foodId: String): FoodItem? {
        val tenantId = TenantSession.tenantId ?: return null
        return try {
            val doc = tenantsCollection
                .document(tenantId)
                .collection("menuItems")
                .document(foodId)
                .get()

            if (doc.exists) {
                val menuItem = doc.data<MenuItem>()
                menuItem.copy(id = doc.id).toFoodItem()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addToCart(
        foodItem: FoodItem,
        quantity: Int,
        selectedOptions: Map<String, String>
    ) {
        // Cart işlemleri CartManager üzerinden yerel olarak yönetiliyor
    }

    override suspend fun getCartItems(): List<FoodItem> {
        return emptyList()
    }

    override suspend fun clearCart() {
    }
}
