package com.bekircaglar.qarko.data.manager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.bekircaglar.qarko.data.model.SavedCard
import com.bekircaglar.qarko.data.model.TrustLevel
import com.bekircaglar.qarko.data.model.User
import kotlinx.datetime.Clock

/**
 * Kullanıcı oturumunu yöneten singleton manager
 */
object UserManager {

    /**
     * Giriş yapmış kullanıcı
     */
    var currentUser: User? by mutableStateOf(null)
        private set

    /**
     * Kullanıcının kayıtlı kartları
     */
    var savedCards: List<SavedCard> by mutableStateOf(emptyList())
        private set

    /**
     * Kullanıcı giriş yapmış mı?
     */
    val isLoggedIn: Boolean
        get() = currentUser != null

    /**
     * Telefon doğrulanmış mı?
     */
    val isPhoneVerified: Boolean
        get() = currentUser?.auth?.isPhoneVerified == true

    /**
     * Kullanıcının güven seviyesi
     */
    val trustLevel: TrustLevel
        get() = currentUser?.trustScore?.level ?: TrustLevel.NEW

    /**
     * Kasada ödeme yapabilir mi?
     */
    val canUseCashPayment: Boolean
        get() = currentUser?.restrictions?.canUseCashPayment == true && isPhoneVerified

    /**
     * Maksimum nakit ödeme tutarı
     */
    val maxCashOrderValue: Double
        get() = currentUser?.restrictions?.maxCashOrderValue ?: 200.0

    /**
     * Kullanıcı engellenmiş mi?
     */
    val isBlocked: Boolean
        get() = currentUser?.restrictions?.isBlocked == true

    /**
     * Kullanıcı bilgisini set eder (giriş sonrası)
     */
    fun updateUser(user: User) {
        currentUser = user
    }

    /**
     * Kayıtlı kartları set eder
     */
    fun updateSavedCards(cards: List<SavedCard>) {
        savedCards = cards
    }

    /**
     * Yeni kart ekler
     */
    fun addSavedCard(card: SavedCard) {
        savedCards = savedCards + card
    }

    /**
     * Kart siler
     */
    fun removeSavedCard(cardId: String) {
        savedCards = savedCards.filter { it.id != cardId }
    }

    /**
     * Varsayılan kartı değiştirir
     */
    fun setDefaultCard(cardId: String) {
        savedCards = savedCards.map { card ->
            card.copy(isDefault = card.id == cardId)
        }
    }

    /**
     * Varsayılan kart
     */
    fun getDefaultCard(): SavedCard? {
        return savedCards.find { it.isDefault } ?: savedCards.firstOrNull()
    }

    /**
     * Oturumu sonlandırır
     */
    fun logout() {
        currentUser = null
        savedCards = emptyList()
    }

    /**
     * Telefon doğrulaması yapıldığında günceller
     */
    fun updatePhoneVerification(verified: Boolean) {
        currentUser = currentUser?.copy(
            auth = currentUser!!.auth.copy(
                isPhoneVerified = verified,
                phoneVerifiedAt = if (verified) Clock.System.now().toEpochMilliseconds() else null
            )
        )
    }

    /**
     * Sipariş verme için gerekli kontrolleri yapar
     * @return Pair<canOrder, errorMessage?>
     */
    fun canPlaceOrder(): Pair<Boolean, String?> {
        return when {
            !isLoggedIn -> Pair(false, "Sipariş vermek için giriş yapmanız gerekiyor")
            isBlocked -> Pair(false, "Hesabınız engellenmiş durumda")
            !isPhoneVerified -> Pair(false, "Sipariş vermek için telefon doğrulaması gerekiyor")
            else -> Pair(true, null)
        }
    }

    /**
     * Kasada ödeme için gerekli kontrolleri yapar
     * @param orderAmount Sipariş tutarı
     * @return Pair<canPay, errorMessage?>
     */
    fun canPayAtCounter(orderAmount: Double): Pair<Boolean, String?> {
        return when {
            !canUseCashPayment -> Pair(false, "Kasada ödeme seçeneği kullanılamıyor")
            orderAmount > maxCashOrderValue -> Pair(
                false,
                "Kasada ödeme için maksimum tutar: ${maxCashOrderValue.toInt()}₺"
            )
            else -> Pair(true, null)
        }
    }
}

