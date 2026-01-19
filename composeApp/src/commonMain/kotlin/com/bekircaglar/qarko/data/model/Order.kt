package com.bekircaglar.qarko.data.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

/**
 * Sipariş modeli
 * Firebase path: tenants/{tenantId}/orders/{orderId}
 */
@Serializable
data class Order(
    val id: String = "",
    val orderNumber: String? = null,
    val tenantId: String = "",
    val userId: String? = null,
    val tableId: String? = null,
    val table: OrderTable? = null,
    val tableNumber: Int? = null,
    val type: OrderType? = OrderType.DINE_IN,
    val items: List<OrderItem> = emptyList(),
    val pricing: OrderPricing? = null,
    val paymentMethod: String? = null,
    val paymentStatus: String? = null,
    val status: OrderStatus = OrderStatus.PENDING,
    val statusHistory: List<StatusHistoryItem> = emptyList(),
    val timing: OrderTiming? = null,
    val notes: String? = null,
    val totalAmount: Double? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

@Serializable
data class OrderTable(
    val id: String = "",
    val name: String = "",
    val section: String? = null
)

@Serializable
enum class OrderType {
    DINE_IN,
    TAKEAWAY,
    DELIVERY
}

@Serializable
data class OrderItem(
    val id: String = "",
    val menuItemId: String? = null,
    val name: String = "",
    val quantity: Int = 1,
    val unitPrice: Double? = null,
    val price: Double = 0.0,
    val originalPrice: Double? = null,
    val totalPrice: Double? = null,
    val removedIngredients: List<String> = emptyList(),
    val customizations: List<OrderItemCustomization> = emptyList(),
    val notes: String? = null
)

@Serializable
data class OrderItemCustomization(
    val groupId: String = "",
    val groupName: String = "",
    val selectedOptions: List<OrderItemCustomizationOption> = emptyList()
)

@Serializable
data class OrderItemCustomizationOption(
    val id: String = "",
    val name: String = "",
    val priceModifier: Double = 0.0
)

@Serializable
data class OrderPricing(
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val discountCode: String? = null,
    val appliedCampaignId: String? = null,
    val tip: Double = 0.0,
    val serviceFee: Double = 0.0,
    val total: Double = 0.0
)

@Serializable
enum class PaymentMethod {
    CREDIT_CARD,
    CASH,
    ONLINE
}

@Serializable
enum class PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED,
    PAID,
    UNPAID
}

@Serializable
enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    DELIVERED,
    SERVED,
    COMPLETED,
    CANCELLED
}

@Serializable
data class StatusHistoryItem(
    val status: OrderStatus,
    val timestamp: Instant,
    val note: String? = null,
    val changedBy: String? = null
)

@Serializable
data class OrderTiming(
    val placedAt: Instant,
    val confirmedAt: Instant? = null,
    val prepStartedAt: Instant? = null,
    val readyAt: Instant? = null,
    val servedAt: Instant? = null,
    val completedAt: Instant? = null,
    val cancelledAt: Instant? = null,
    val estimatedPrepTime: Int? = null
)
