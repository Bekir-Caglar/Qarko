package com.bekircaglar.qarko.data.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Order(
    val id: String = "",
    val orderNumber: String? = null,
    val tenantId: String = "",
    val tenantName: String? = null,
    val tenantLogoUrl: String? = null,
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
    // statusHistory ve timing alanlarını şimdilik devre dışı bırakıyoruz
    // çünkü Firebase Timestamp formatı kotlinx.datetime ile uyumsuz
    // val statusHistory: List<StatusHistoryItem> = emptyList(),
    // val timing: OrderTiming? = null,
    val notes: String? = null,
    val totalAmount: Double? = null,
    val createdAt: @Serializable(with = SafeInstantSerializer::class) Instant? = null,
    val updatedAt: @Serializable(with = SafeInstantSerializer::class) Instant? = null
)

/**
 * Firebase Timestamp veya ISO 8601 formatını güvenli şekilde parse eden serializer.
 * Parse başarısız olursa null döner.
 */
object SafeInstantSerializer : KSerializer<Instant?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SafeInstant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant?) {
        if (value != null) {
            encoder.encodeString(value.toString())
        }
    }

    override fun deserialize(decoder: Decoder): Instant? {
        return try {
            when (val decodedValue = decoder.decodeString()) {
                "" -> null
                else -> parseFirebaseTimestamp(decodedValue) ?: Instant.parse(decodedValue)
            }
        } catch (e: Exception) {
            println("SafeInstantSerializer: Parse failed, returning null. Error: ${e.message}")
            null
        }
    }

    private fun parseFirebaseTimestamp(value: String): Instant? {
        // Format: "Timestamp(seconds=1769078177, nanoseconds=270000000)"
        if (value.startsWith("Timestamp(")) {
            val regex = """seconds=(\d+).*nanoseconds=(\d+)""".toRegex()
            val match = regex.find(value)
            if (match != null) {
                val seconds = match.groupValues[1].toLong()
                val nanoseconds = match.groupValues[2].toLong()
                return Instant.fromEpochSeconds(seconds, nanoseconds)
            }
        }
        return null
    }
}

@Serializable(with = OrderStatusSerializer::class)
enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    DELIVERED,
    SERVED,
    COMPLETED,
    CANCELLED;

    companion object {
        fun fromString(value: String?): OrderStatus {
            return when (value?.uppercase()) {
                "PENDING" -> PENDING
                "CONFIRMED", "APPROVED", "ACCEPTED" -> CONFIRMED
                "PREPARING" -> PREPARING
                "READY" -> READY
                "DELIVERED" -> DELIVERED
                "SERVED" -> SERVED
                "COMPLETED", "FINISHED" -> COMPLETED
                "CANCELLED", "REJECTED" -> CANCELLED
                else -> PENDING
            }
        }
    }
}

object OrderStatusSerializer : KSerializer<OrderStatus> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("OrderStatus", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: OrderStatus) = encoder.encodeString(value.name)
    override fun deserialize(decoder: Decoder): OrderStatus = OrderStatus.fromString(decoder.decodeString())
}

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

// Bu sınıflar şimdilik kullanılmıyor - Firebase Timestamp sorunu çözülene kadar
@Serializable
data class StatusHistoryItem(
    val status: OrderStatus = OrderStatus.PENDING,
    val timestamp: String? = null, // String olarak sakla, timestamp parse sorununu önle
    val note: String? = null,
    val changedBy: String? = null
)

@Serializable
data class OrderTiming(
    val placedAt: String? = null,
    val confirmedAt: String? = null,
    val prepStartedAt: String? = null,
    val readyAt: String? = null,
    val servedAt: String? = null,
    val completedAt: String? = null,
    val cancelledAt: String? = null,
    val estimatedPrepTime: Int? = null
)
