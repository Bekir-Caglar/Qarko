package com.bekircaglar.qarko.data.repository

import com.bekircaglar.qarko.data.model.Order
import com.bekircaglar.qarko.domain.repository.IOrderRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OrderRepository : IOrderRepository {
    private val firestore = Firebase.firestore
    private val tenantsCollection = firestore.collection("tenants")

    override suspend fun createOrder(order: Order): Result<String> {
        return try {
            println("OrderRepository: Creating order with userId=${order.userId}, tenantId=${order.tenantId}")
            val orderRef = tenantsCollection
                .document(order.tenantId)
                .collection("orders")
                .add(order)

            println("OrderRepository: Order created successfully with id=${orderRef.id}")
            Result.success(orderRef.id)
        } catch (e: Exception) {
            println("OrderRepository: Error creating order: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Kullanıcının TÜM işletmelerdeki siparişlerini real-time olarak dinler.
     * Durum değişiklikleri (onay, hazırlık vb.) anlık olarak yansır.
     */
    override fun observeUserOrders(userId: String): Flow<List<Order>> {
        println("OrderRepository: observeUserOrders called with userId=$userId")
        
        return firestore
            .collectionGroup("orders")
            .where { "userId" equalTo userId }
            .orderBy("createdAt", Direction.DESCENDING)
            .snapshots
            .map { snapshot ->
                println("OrderRepository: Received snapshot with ${snapshot.documents.size} documents")
                
                snapshot.documents.mapNotNull { doc ->
                    try {
                        println("OrderRepository: Processing document ${doc.id}")
                        // encodeDefaults = true ile decode et - eksik alanlar için default değerler kullanılır
                        val order = doc.data<Order>(
                            strategy = Order.serializer()
                        )
                        println("OrderRepository: Parsed order - id=${doc.id}, userId=${order.userId}, status=${order.status}, orderNumber=${order.orderNumber}")
                        order.copy(id = doc.id)
                    } catch (e: Exception) {
                        println("OrderRepository: Error parsing order ${doc.id}: ${e.message}")
                        println("OrderRepository: Exception type: ${e::class.simpleName}")
                        e.printStackTrace()
                        null
                    }
                }
            }
    }
}

