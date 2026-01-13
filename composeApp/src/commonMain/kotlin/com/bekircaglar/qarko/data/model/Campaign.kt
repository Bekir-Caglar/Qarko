package com.bekircaglar.qarko.data.model

import kotlinx.serialization.Serializable

/**
 * Kampanya modeli
 * Firebase path: tenants/{tenantId}/campaigns/{campaignId}
 */
@Serializable
data class Campaign(
    val id: String = "",
    val tenantId: String = "",
    val title: String = "",
    val description: String = "",
    val emoji: String = "🎁",
    val imageUrl: String = "",
    val type: CampaignType = CampaignType.PERCENTAGE_DISCOUNT,
    val discountValue: Double = 0.0, // Yüzde veya sabit tutar
    val validity: CampaignValidity = CampaignValidity(),
    val conditions: CampaignConditions = CampaignConditions(),
    val usage: CampaignUsage = CampaignUsage(),
    val code: String? = null, // Promosyon kodu (opsiyonel)
    val isActive: Boolean = true,
    val sortOrder: Int = 0
)

/**
 * Kampanya türü
 */
@Serializable
enum class CampaignType {
    PERCENTAGE_DISCOUNT,    // Yüzde indirim (örn: %20)
    FIXED_DISCOUNT,         // Sabit tutar indirim (örn: 50₺)
    BUY_X_GET_Y,           // X al Y öde
    FREE_ITEM,             // Bedava ürün
    FREE_DELIVERY,         // Ücretsiz teslimat
    FIRST_ORDER            // İlk sipariş indirimi
}

/**
 * Kampanya geçerlilik bilgisi
 */
@Serializable
data class CampaignValidity(
    val startDate: Long = 0, // timestamp
    val endDate: Long = 0, // timestamp
    val isActive: Boolean = true,
    val validDays: List<Int> = listOf(0, 1, 2, 3, 4, 5, 6), // 0 = Pazar, 6 = Cumartesi
    val validHoursStart: String = "00:00",
    val validHoursEnd: String = "23:59"
)

/**
 * Kampanya koşulları
 */
@Serializable
data class CampaignConditions(
    val minOrderAmount: Double = 0.0, // Minimum sepet tutarı
    val maxDiscountAmount: Double? = null, // Maksimum indirim tutarı
    val maxUsagePerUser: Int? = null, // Kullanıcı başına kullanım limiti
    val maxTotalUsage: Int? = null, // Toplam kullanım limiti
    val applicableCategories: List<String> = emptyList(), // Geçerli kategoriler (boş = tümü)
    val applicableItems: List<String> = emptyList(), // Geçerli ürünler (boş = tümü)
    val excludedCategories: List<String> = emptyList(), // Hariç kategoriler
    val excludedItems: List<String> = emptyList(), // Hariç ürünler
    val requiresLogin: Boolean = true, // Giriş gerekli mi?
    val firstOrderOnly: Boolean = false, // Sadece ilk sipariş için mi?
    val requiredItemId: String? = null, // X al Y öde için gerekli ürün
    val freeItemId: String? = null // Bedava verilecek ürün
)

/**
 * Kampanya kullanım istatistikleri
 */
@Serializable
data class CampaignUsage(
    val totalUsed: Int = 0,
    val totalDiscountGiven: Double = 0.0,
    val lastUsedAt: Long? = null
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

