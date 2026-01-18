package com.bekircaglar.qarko.data.repository

import com.bekircaglar.qarko.data.model.Order
import com.bekircaglar.qarko.domain.repository.IOrderRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class OrderRepository : IOrderRepository {
    private val firestore = Firebase.firestore
    private val tenantsCollection = firestore.collection("tenants")

    override suspend fun createOrder(order: Order): Result<String> {
        return try {
            val orderRef = tenantsCollection
                .document(order.tenantId)
                .collection("orders")
                .add(order)
            
            Result.success(orderRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
