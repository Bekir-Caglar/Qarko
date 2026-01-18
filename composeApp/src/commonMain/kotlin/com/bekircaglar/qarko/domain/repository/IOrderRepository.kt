package com.bekircaglar.qarko.domain.repository

import com.bekircaglar.qarko.data.model.Order

interface IOrderRepository {
    suspend fun createOrder(order: Order): Result<String>
}
