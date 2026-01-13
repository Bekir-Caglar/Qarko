package com.bekircaglar.qarko.data.model

import kotlinx.serialization.Serializable

/**
 * Sipariş modeli
 * Firebase path: tenants/{tenantId}/orders/{orderId}
 */
@Serializable
data class Order(
    val id: String = "",
    val tenantId: String = "",
    val tableId: String? = null,
    val tableName: String? = null,
    val userId: String? = null, // null ise misafir sipariş
    val userPhone: String? = null,
    val orderNumber: String = "",
    val type: OrderType = OrderType.DINE_IN,
    val status: OrderStatus = OrderStatus.PENDING,
    val items: List<OrderItem> = emptyList(),
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val paymentMethod: PaymentMethod? = null,
    val notes: String = "",
    val campaignId: String? = null,
    val campaignCode: String? = null,
    val statusHistory: List<OrderStatusHistory> = emptyList(),
    val createdAt: Long = 0, // timestamp
    val updatedAt: Long = 0
)

/**
 * Sipariş kalemi
 */
@Serializable
data class OrderItem(
    val id: String = "",
    val menuItemId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0,
    val customizations: List<OrderItemCustomization> = emptyList(),
    val removedItems: List<String> = emptyList(),
    val notes: String = ""
)

/**
 * Sipariş kalemi özelleştirmesi
 */
@Serializable
data class OrderItemCustomization(
    val groupId: String = "",
    val groupName: String = "",
    val optionId: String = "",
    val optionName: String = "",
    val extraPrice: Double = 0.0
)

/**
 * Sipariş türü
 */
@Serializable
enum class OrderType {
    DINE_IN,    // Masa siparişi
    TAKEAWAY,   // Paket servis
    DELIVERY    // Teslimat
}

/**
 * Sipariş durumu
 */
@Serializable
enum class OrderStatus {
    PENDING,    // Beklemede
    CONFIRMED,  // Onaylandı
    PREPARING,  // Hazırlanıyor
    READY,      // Hazır
    SERVED,     // Servis edildi
    COMPLETED,  // Tamamlandı
    CANCELLED   // İptal edildi
}

/**
 * Ödeme durumu
 */
@Serializable
enum class PaymentStatus {
    PENDING,    // Ödeme bekleniyor
    PAID,       // Ödendi
    UNPAID,     // Ödenmedi
    REFUNDED    // İade edildi
}

/**
 * Ödeme yöntemi
 */
@Serializable
enum class PaymentMethod {
    CASH,           // Nakit
    CREDIT_CARD,    // Kredi kartı (online)
    CASH_AT_COUNTER // Kasada öde
}

/**
 * Sipariş durum geçmişi
 */
@Serializable
data class OrderStatusHistory(
    val status: OrderStatus,
    val timestamp: Long,
    val changedBy: String? = null, // userId veya "SYSTEM"
    val note: String? = null
)

