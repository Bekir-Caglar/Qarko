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
    val payment: OrderPayment? = null,
    // Alternative plain fields if used directly outside of payment obj
    val paymentMethod: String? = null, // PaymentMethod enum string or custom
    val paymentStatus: String? = null, // PaymentStatus enum string
    val status: OrderStatus = OrderStatus.PENDING,
    val statusHistory: List<StatusHistoryItem> = emptyList(),
    val timing: OrderTiming? = null,
    val notes: String? = null, // or OrderNotes if object? TS says notes?: string OR OrderNotes interface below it?
    // TS says: "notes?: string;" in Order interface, but also "export interface OrderNotes". Maybe it's changed.
    // I'll stick to string as per Order interface definition "notes?: string". Wait, context has `OrderNotes` interface but Order uses `notes?: string`.
    // Actually in the shared TS block: `notes?: string;` inside Order interface. But there is `OrderNotes` interface too.
    // I will use String for `notes` property in Order.
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
    val totalPrice: Double? = null,
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
    val tip: Double = 0.0,
    val serviceFee: Double = 0.0,
    val total: Double = 0.0
)

@Serializable
data class OrderPayment(
    val method: PaymentMethod,
    val status: PaymentStatus,
    val transactionId: String? = null,
    val paidAt: Instant? = null
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
    // Additional from TS union type if strictly needed, but Enum usually covers fixed set.
    // TS: PaymentStatus | 'PAID' | 'UNPAID'.
    // I'll assume standard ones.
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
    val completedAt: Instant? = null, // Renamed from deliveredAt if needed, TS has completedAt
    val cancelledAt: Instant? = null,
    val estimatedPrepTime: Int? = null // minutes
)
