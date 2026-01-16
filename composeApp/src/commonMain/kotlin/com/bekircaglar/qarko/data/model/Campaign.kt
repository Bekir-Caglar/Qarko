package com.bekircaglar.qarko.data.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Kampanya modeli
 * Firebase path: tenants/{tenantId}/campaigns/{campaignId}
 */
@Serializable
data class Campaign(
    val id: String = "",
    val name: String = "",
    val title: String = "",
    val description: String = "",
    val type: CampaignType = CampaignType.PERCENTAGE_DISCOUNT,
    val discountValue: Double = 0.0,
    val conditions: CampaignConditions = CampaignConditions(),
    val validity: CampaignValidity = CampaignValidity(),
    val usage: CampaignUsage = CampaignUsage(),
    val imageUrl: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

/**
 * Kampanya türü
 */
@Serializable
enum class CampaignType {
    PERCENTAGE_DISCOUNT,    // Yüzde indirim (örn: %20)
    FIXED_DISCOUNT,         // Sabit tutar indirim (örn: 50₺)
    BUY_X_GET_Y,           // X al Y öde
    FREE_ITEM              // Bedava ürün
}

@Serializable
data class CampaignConditions(
    val minOrderAmount: Double? = null, // Minimum sepet tutarı
    val maxDiscountAmount: Double? = null, // Maksimum indirim tutarı
    val applicableCategories: List<String>? = null, // Geçerli kategoriler (boş = tümü)
    val applicableItems: List<String>? = null, // Geçerli ürünler (boş = tümü)
    val applicableOrderTypes: List<String>? = null, // 'DINE_IN' | 'TAKEAWAY' | 'DELIVERY'
    val applicableDays: List<Int>? = null, // 0-6, Sunday = 0
    val applicableHours: CampaignHours? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val freeItemId: String? = null, // Bedava verilecek ürün
    val maxUsagePerUser: Int? = null, // Kullanıcı başına kullanım limiti
    val maxTotalUsage: Int? = null, // Toplam kullanım limiti
    val requiresCode: Boolean? = null, // Kod gerekli mi?
    val code: String? = null // Promosyon kodu (opsiyonel)
)

@Serializable
data class CampaignHours(
    val start: String = "00:00",
    val end: String = "23:59"
)

/**
 * Kampanya geçerlilik bilgisi
 */
@Serializable
data class CampaignValidity(
    val startDate: Instant? = null, // Başlangıç tarihi
    val endDate: Instant? = null, // Bitiş tarihi
    val isActive: Boolean = true
)

/**
 * Kampanya kullanım istatistikleri
 */
@Serializable
data class CampaignUsage(
    val totalUsed: Int = 0,
    val totalDiscountGiven: Double = 0.0,
    val usageByDate: Map<String, Int>? = null
)

/**
 * Kullanıcının kampanya kullanım durumu
 */
@Serializable
data class UserCampaignUsage(
    val campaignId: String = "",
    val userId: String = "",
    val usageCount: Int = 0,
    val lastUsedAt: Long? = null,
    val totalDiscountReceived: Double = 0.0
)
