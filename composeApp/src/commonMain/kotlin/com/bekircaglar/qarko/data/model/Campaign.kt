package com.bekircaglar.qarko.data.model

import kotlinx.serialization.Serializable
import dev.gitlive.firebase.firestore.Timestamp

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
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
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
enum class CampaignScope {
    ALL,
    CATEGORY,
    ITEM
}

@Serializable
data class CampaignConditions(
    val minOrderAmount: Double? = null, 
    val maxDiscountAmount: Double? = null, 
    val scope: CampaignScope = CampaignScope.ALL,
    val applicableCategories: List<String>? = null, 
    val applicableItems: List<String>? = null, 
    val applicableOrderTypes: List<String>? = null, 
    val applicableDays: List<Int>? = null, 
    val applicableHours: CampaignHours? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val freeItemId: String? = null, 
    val maxUsagePerUser: Int? = null, 
    val maxTotalUsage: Int? = null, 
    val requiresCode: Boolean? = null, 
    val code: String? = null 
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
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
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
    val lastUsedAt: Timestamp? = null,
    val totalDiscountReceived: Double = 0.0
)
