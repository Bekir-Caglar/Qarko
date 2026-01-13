package com.bekircaglar.qarko.data.model

import kotlinx.serialization.Serializable

/**
 * Masa modeli
 * Firebase path: tenants/{tenantId}/tables/{tableId}
 */
@Serializable
data class Table(
    val id: String = "",
    val name: String = "", // Masa adı/numarası (örn: "Masa 5", "Bahçe 1")
    val section: String = "", // Bölüm (Bahçe, Teras, Salon)
    val capacity: Int = 4, // Kişi kapasitesi
    val status: TableStatus = TableStatus.AVAILABLE,
    val currentOrder: CurrentOrderInfo? = null,
    val qrCode: QRCodeInfo? = null
)

/**
 * Masa durumu
 */
@Serializable
enum class TableStatus {
    AVAILABLE,       // Müsait
    OCCUPIED,        // Dolu
    RESERVED,        // Rezerve
    HAS_ORDER,       // Sipariş var
    PAYMENT_PENDING  // Ödeme bekleniyor
}

/**
 * Aktif sipariş özet bilgisi
 */
@Serializable
data class CurrentOrderInfo(
    val orderId: String = "",
    val orderNumber: String = "",
    val totalAmount: Double = 0.0
)

/**
 * QR kod bilgisi
 */
@Serializable
data class QRCodeInfo(
    val url: String = "",
    val imageUrl: String = ""
)

