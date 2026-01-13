package com.bekircaglar.qarko.data.model

import kotlinx.serialization.Serializable

/**
 * Kullanıcı modeli
 * Firebase path: users/{userId}
 */
@Serializable
data class User(
    val id: String = "",
    val firebaseUid: String = "",
    val auth: UserAuth = UserAuth(),
    val trustScore: UserTrustScore = UserTrustScore(),
    val restrictions: UserRestrictions = UserRestrictions(),
    val profile: UserProfile = UserProfile(),
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)

/**
 * Kullanıcı auth bilgileri
 */
@Serializable
data class UserAuth(
    val providers: List<String> = emptyList(), // "phone", "google.com", "apple.com"
    val primaryProvider: String = "",
    val phoneNumber: String? = null,
    val email: String? = null,
    val isPhoneVerified: Boolean = false,
    val isEmailVerified: Boolean = false,
    val phoneVerifiedAt: Long? = null,
    val lastSignInAt: Long? = null
)

/**
 * Kullanıcı güven skoru
 */
@Serializable
data class UserTrustScore(
    val level: TrustLevel = TrustLevel.NEW,
    val score: Int = 0, // 0-100
    val totalOrders: Int = 0,
    val successfulOrders: Int = 0,
    val cancelledOrders: Int = 0,
    val noShowOrders: Int = 0,
    val totalSpent: Double = 0.0,
    val memberSince: Long = 0,
    val lastOrderAt: Long? = null
)

/**
 * Güven seviyesi
 */
@Serializable
enum class TrustLevel {
    NEW,        // Yeni kullanıcı (0 sipariş)
    TRUSTED,    // Güvenilir (1-5 başarılı sipariş)
    VIP         // VIP (5+ başarılı sipariş)
}

/**
 * Kullanıcı kısıtlamaları
 */
@Serializable
data class UserRestrictions(
    val canUseCashPayment: Boolean = true,
    val maxCashOrderValue: Double = 200.0,
    val requiresManualApproval: Boolean = false,
    val isBlocked: Boolean = false,
    val blockReason: String? = null,
    val blockedAt: Long? = null
)

/**
 * Kullanıcı profil bilgileri
 */
@Serializable
data class UserProfile(
    val firstName: String = "",
    val lastName: String = "",
    val displayName: String = "",
    val photoUrl: String? = null
)

/**
 * Kayıtlı ödeme kartı
 */
@Serializable
data class SavedCard(
    val id: String = "",
    val userId: String = "",
    val cardName: String = "", // Kullanıcının verdiği isim (örn: "Ziraat Kartım")
    val cardBrand: CardBrand = CardBrand.UNKNOWN,
    val lastFourDigits: String = "",
    val firstSixDigits: String = "", // BIN numarası
    val expiryMonth: Int = 0,
    val expiryYear: Int = 0,
    val cardHolderName: String = "",
    val isDefault: Boolean = false,
    val createdAt: Long = 0,
    // Güvenlik: Kart token'ı ödeme sağlayıcı tarafından verilir
    val paymentToken: String = ""
)

/**
 * Kart markası
 */
@Serializable
enum class CardBrand {
    VISA,
    MASTERCARD,
    AMEX,
    TROY,
    UNKNOWN
}

