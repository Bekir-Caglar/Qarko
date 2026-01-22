package com.bekircaglar.qarko.domain.repository

import com.bekircaglar.qarko.data.model.Order
import kotlinx.coroutines.flow.Flow

interface IOrderRepository {
    suspend fun createOrder(order: Order): Result<String>
    
    /**
     * Kullanıcının tüm işletmelerdeki siparişlerini real-time olarak dinler
     * @param userId Kullanıcı ID'si
     * @return Siparişlerin Flow'u
     */
    fun observeUserOrders(userId: String): Flow<List<Order>>
}
