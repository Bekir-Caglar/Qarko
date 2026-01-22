package com.bekircaglar.qarko.presentation.checkout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.qarko.data.manager.CartManager
import com.bekircaglar.qarko.data.manager.TenantSession
import com.bekircaglar.qarko.data.manager.UserManager
import com.bekircaglar.qarko.data.model.*
import com.bekircaglar.qarko.domain.repository.ITenantRepository
import com.bekircaglar.qarko.domain.usecase.order.CreateOrderUseCase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class CheckoutViewModel(
    private val createOrderUseCase: CreateOrderUseCase,
    private val tenantRepository: ITenantRepository
) : ViewModel() {

    var uiState by mutableStateOf(CheckoutUiState())
        private set

    private val _events = MutableSharedFlow<CheckoutEvent>()
    val events = _events.asSharedFlow()

    private val firestore = Firebase.firestore

    init {
        loadCampaigns()
    }

    fun loadCampaigns() {
        val tenantId = TenantSession.tenantId ?: return
        val userId = UserManager.currentUser?.id
        viewModelScope.launch {
            uiState = uiState.copy(isCampaignsLoading = true)
            try {
                val campaignsSnapshot = firestore.collection("tenants")
                    .document(tenantId)
                    .collection("campaigns")
                    .get()

                val campaigns = campaignsSnapshot.documents.map { it.data<Campaign>().copy(id = it.id) }

                val userUsages = if (userId != null) {
                    try {
                        val snapshot = firestore.collection("users")
                            .document(userId)
                            .collection("campaignUsages")
                            .get()
                        snapshot.documents.associate { it.id to it.data<UserCampaignUsage>() }
                    } catch (e: Exception) {
                        emptyMap()
                    }
                } else emptyMap()

                validateCampaigns(campaigns, userUsages)
            } catch (e: Exception) {
                uiState = uiState.copy(isCampaignsLoading = false)
            }
        }
    }

    private fun validateCampaigns(campaigns: List<Campaign>, userUsages: Map<String, UserCampaignUsage>) {
        val cartTotal = CartManager.totalPrice
        val nowSeconds = Clock.System.now().epochSeconds
        val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val currentDay = currentDateTime.dayOfWeek.ordinal 
        val currentTimeStr = "${currentDateTime.hour.toString().padStart(2, '0')}:${currentDateTime.minute.toString().padStart(2, '0')}"

        val available = mutableListOf<Campaign>()
        val others = mutableListOf<Pair<Campaign, String>>()
        val discounts = mutableMapOf<String, Double>()

        campaigns.filter { it.validity.isActive }.forEach { campaign ->
            val conditions = campaign.conditions
            val validity = campaign.validity

            // Tarih Kontrolü
            if (validity.startDate != null && nowSeconds < validity.startDate.seconds) {
                others.add(campaign to "Bu kampanya henüz başlamadı.")
                return@forEach
            }
            if (validity.endDate != null && nowSeconds > validity.endDate.seconds) {
                return@forEach
            }

            // Kullanıcı Limit Kontrolü
            val userUsage = userUsages[campaign.id]
            if (conditions.maxUsagePerUser != null && (userUsage?.usageCount ?: 0) >= conditions.maxUsagePerUser) {
                others.add(campaign to "Bu kampanya için kullanım hakkınız doldu.")
                return@forEach
            }

            // Genel Limit Kontrolü
            if (conditions.maxTotalUsage != null && campaign.usage.totalUsed >= conditions.maxTotalUsage) {
                others.add(campaign to "Bu kampanya genel kullanım limitine ulaştı.")
                return@forEach
            }

            // Saat Kontrolü
            if (conditions.applicableHours != null) {
                if (currentTimeStr < conditions.applicableHours.start || currentTimeStr > conditions.applicableHours.end) {
                    others.add(campaign to "Bu kampanya sadece ${conditions.applicableHours.start} - ${conditions.applicableHours.end} saatleri arasında geçerlidir.")
                    return@forEach
                }
            }

            // Gün Kontrolü
            if (conditions.applicableDays != null && currentDay !in conditions.applicableDays) {
                others.add(campaign to "Bu kampanya bugün geçerli değil.")
                return@forEach
            }

            // Kampanya Kodu Kontrolü
            if (conditions.requiresCode == true && uiState.appliedPromoCode != conditions.code) {
                return@forEach
            }

            // Sepet Tutarı Kontrolü
            if (conditions.minOrderAmount != null && cartTotal < conditions.minOrderAmount) {
                val remaining = conditions.minOrderAmount - cartTotal
                others.add(campaign to "Sepet tutarınız: ₺${cartTotal.toInt()} (₺${remaining.toInt()} daha gerekli)")
                return@forEach
            }

            val effectiveScope = when {
                !conditions.applicableItems.isNullOrEmpty() -> CampaignScope.ITEM
                !conditions.applicableCategories.isNullOrEmpty() -> CampaignScope.CATEGORY
                else -> conditions.scope
            }

            // X Al Y Öde Kontrolü - AYNI ÜRÜNDEN X ADET MANTIĞI
            if (campaign.type == CampaignType.BUY_X_GET_Y) {
                val buyX = conditions.buyQuantity ?: 1
                
                val applicableItems = when (effectiveScope) {
                    CampaignScope.ITEM -> {
                        val ids = (conditions.applicableItems ?: emptyList()).map { it.trim() }
                        CartManager.cartItems.filter { it.foodId.trim() in ids }
                    }
                    CampaignScope.CATEGORY -> {
                        val cats = (conditions.applicableCategories ?: emptyList()).map { it.trim() }
                        CartManager.cartItems.filter { it.categoryId.trim() in cats }
                    }
                    else -> CartManager.cartItems
                }
                
                val hasRequirementMet = applicableItems.any { it.quantity >= buyX }
                
                if (!hasRequirementMet) {
                    val message = if (applicableItems.isEmpty()) {
                        "Bu kampanya için geçerli ürün eklemelisiniz."
                    } else {
                        "Aynı üründen en az $buyX adet eklemelisiniz."
                    }
                    others.add(campaign to message)
                    return@forEach
                }
            } else {
                // Diğer kampanya türleri için genel kapsam kontrolü
                val isScopeValid = when (effectiveScope) {
                    CampaignScope.ALL -> true
                    CampaignScope.CATEGORY -> {
                        val applicableCategories = (conditions.applicableCategories ?: emptyList()).map { it.trim() }
                        CartManager.cartItems.any { it.categoryId.trim() in applicableCategories }
                    }
                    CampaignScope.ITEM -> {
                        val applicableItems = (conditions.applicableItems ?: emptyList()).map { it.trim() }
                        CartManager.cartItems.any { it.foodId.trim() in applicableItems }
                    }
                }

                if (!isScopeValid) {
                    val reason = when (effectiveScope) {
                        CampaignScope.CATEGORY -> "Bu kampanya belirli kategorilerde geçerlidir."
                        CampaignScope.ITEM -> "Bu kampanya belirli ürünlerde geçerlidir."
                        else -> "Bu kampanya sepetinizdeki ürünler için geçerli değildir."
                    }
                    others.add(campaign to reason)
                    return@forEach
                }
            }

            // Ücretsiz Ürün Kontrolü
            if (campaign.type == CampaignType.FREE_ITEM) {
                val hasFreeItem = CartManager.cartItems.any { it.foodId.trim() == conditions.freeItemId?.trim() }
                if (!hasFreeItem) {
                    others.add(campaign to "Hediye ürünü sepetinize eklemelisiniz.")
                    return@forEach
                }
            }

            val discountVal = calculateDiscountValue(campaign, effectiveScope)
            if (discountVal > 0) {
                discounts[campaign.id] = discountVal
                available.add(campaign)
            } else {
                others.add(campaign to "Bu kampanya sepetinizde indirim sağlamıyor.")
            }
        }

        uiState = uiState.copy(
            availableCampaigns = available,
            otherCampaigns = others,
            campaignDiscounts = discounts,
            isCampaignsLoading = false
        )
        
        validateSelectedCampaign(available)
    }

    private fun validateSelectedCampaign(available: List<Campaign>) {
        if (uiState.selectedCampaign != null) {
            val found = available.find { it.id == uiState.selectedCampaign!!.id }
            if (found == null) {
                selectCampaign(null)
            } else {
                calculateDiscount(found)
            }
        }
        
        // Sepet 0 TL ise sadece kasada öde seçilsin
        val finalAmount = CartManager.totalPrice - uiState.discountAmount
        if (finalAmount <= 0) {
            uiState = uiState.copy(selectedPaymentMethodIndex = 1)
        }
    }

    private fun calculateDiscountValue(campaign: Campaign, effectiveScope: CampaignScope): Double {
        val cartTotal = CartManager.totalPrice
        val cartItems = CartManager.cartItems

        return when (campaign.type) {
            CampaignType.PERCENTAGE_DISCOUNT -> {
                val applicableItems = when (effectiveScope) {
                    CampaignScope.ALL -> cartItems
                    CampaignScope.CATEGORY -> {
                        val cats = (campaign.conditions.applicableCategories ?: emptyList()).map { it.trim() }
                        cartItems.filter { it.categoryId.trim() in cats }
                    }
                    CampaignScope.ITEM -> {
                        val items = (campaign.conditions.applicableItems ?: emptyList()).map { it.trim() }
                        cartItems.filter { it.foodId.trim() in items }
                    }
                }
                
                if (effectiveScope == CampaignScope.ALL) {
                    val amount = cartTotal * (campaign.discountValue / 100)
                    campaign.conditions.maxDiscountAmount?.let { minOf(amount, it) } ?: amount
                } else {
                    val totalBaseApplicable = applicableItems.sumOf { it.basePrice * it.quantity }
                    val amount = totalBaseApplicable * (campaign.discountValue / 100)
                    campaign.conditions.maxDiscountAmount?.let { minOf(amount, it) } ?: amount
                }
            }
            
            CampaignType.FIXED_DISCOUNT -> {
                val applicableItems = when (effectiveScope) {
                    CampaignScope.ALL -> cartItems
                    CampaignScope.CATEGORY -> {
                        val cats = (campaign.conditions.applicableCategories ?: emptyList()).map { it.trim() }
                        cartItems.filter { it.categoryId.trim() in cats }
                    }
                    CampaignScope.ITEM -> {
                        val items = (campaign.conditions.applicableItems ?: emptyList()).map { it.trim() }
                        cartItems.filter { it.foodId.trim() in items }
                    }
                }
                
                val totalBaseApplicable = applicableItems.sumOf { it.basePrice * it.quantity }
                minOf(campaign.discountValue, totalBaseApplicable)
            }

            CampaignType.BUY_X_GET_Y -> {
                val buyX = campaign.conditions.buyQuantity ?: 1
                val getY = campaign.conditions.getQuantity ?: 1
                val freeCountPerSet = buyX - getY
                
                val applicableItems = when (effectiveScope) {
                    CampaignScope.ITEM -> {
                        val ids = (campaign.conditions.applicableItems ?: emptyList()).map { it.trim() }
                        cartItems.filter { it.foodId.trim() in ids }
                    }
                    CampaignScope.CATEGORY -> {
                        val cats = (campaign.conditions.applicableCategories ?: emptyList()).map { it.trim() }
                        cartItems.filter { it.categoryId.trim() in cats }
                    }
                    else -> cartItems
                }

                var totalDiscount = 0.0
                applicableItems.filter { it.quantity >= buyX }.forEach { item ->
                    val setSets = item.quantity / buyX
                    val freeItems = setSets * freeCountPerSet
                    totalDiscount += freeItems * item.basePrice
                }
                totalDiscount
            }

            CampaignType.FREE_ITEM -> {
                val freeItemId = campaign.conditions.freeItemId?.trim()
                val freeItemInCart = cartItems.find { it.foodId.trim() == freeItemId }
                freeItemInCart?.basePrice ?: 0.0
            }
        }
    }

    fun applyPromoCode(code: String) {
        val tenantId = TenantSession.tenantId ?: return
        viewModelScope.launch {
            uiState = uiState.copy(isPromoCodeLoading = true)
            try {
                val snapshot = firestore.collection("tenants")
                    .document(tenantId)
                    .collection("campaigns")
                    .where("conditions.code", equalTo = code)
                    .get()

                val campaignDoc = snapshot.documents.firstOrNull()
                if (campaignDoc != null) {
                    val campaign = campaignDoc.data<Campaign>().copy(id = campaignDoc.id)
                    
                    val cartTotal = CartManager.totalPrice
                    if (campaign.conditions.minOrderAmount != null && cartTotal < campaign.conditions.minOrderAmount) {
                        uiState = uiState.copy(promoCodeError = "Bu kod için minimum ₺${campaign.conditions.minOrderAmount.toInt()} sepet tutarı gereklidir.")
                    } else {
                        uiState = uiState.copy(appliedPromoCode = code, promoCodeError = null)
                        loadCampaigns()
                        selectCampaign(campaign)
                    }
                } else {
                    uiState = uiState.copy(promoCodeError = "Geçersiz kampanya kodu.")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(promoCodeError = "Bir hata oluştu.")
            } finally {
                uiState = uiState.copy(isPromoCodeLoading = false)
            }
        }
    }

    fun selectCampaign(campaign: Campaign?) {
        uiState = uiState.copy(selectedCampaign = campaign)
        calculateDiscount(campaign)
    }

    private fun calculateDiscount(campaign: Campaign?) {
        if (campaign == null) {
            uiState = uiState.copy(discountAmount = 0.0)
            checkZeroTotal()
            return
        }

        val effectiveScope = when {
            !campaign.conditions.applicableItems.isNullOrEmpty() -> CampaignScope.ITEM
            !campaign.conditions.applicableCategories.isNullOrEmpty() -> CampaignScope.CATEGORY
            else -> campaign.conditions.scope
        }
        
        val cartTotal = CartManager.totalPrice
        val calculatedDiscount = calculateDiscountValue(campaign, effectiveScope)
        
        uiState = uiState.copy(discountAmount = minOf(calculatedDiscount, cartTotal))
        checkZeroTotal()
    }

    private fun checkZeroTotal() {
        val finalAmount = CartManager.totalPrice - uiState.discountAmount
        if (finalAmount <= 0) {
            uiState = uiState.copy(selectedPaymentMethodIndex = 1)
        }
    }

    fun onOrderNoteChange(note: String) {
        uiState = uiState.copy(orderNote = note)
    }

    fun onPaymentMethodChange(index: Int) {
        // Eğer sepet 0 TL ise ödeme yöntemi değişikliğine izin verme (sadece kasada öde seçili kalsın)
        val finalAmount = CartManager.totalPrice - uiState.discountAmount
        if (finalAmount <= 0 && index == 0) return
        
        uiState = uiState.copy(selectedPaymentMethodIndex = index)
    }

    fun placeOrder() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            
            val paymentMethod = if (uiState.selectedPaymentMethodIndex == 0) {
                PaymentMethod.ONLINE
            } else {
                PaymentMethod.CASH
            }

            val result = createOrderUseCase(
                paymentMethod = paymentMethod,
                notes = uiState.orderNote,
                discountAmount = uiState.discountAmount,
                discountCode = uiState.selectedCampaign?.conditions?.code ?: uiState.appliedPromoCode,
                campaignId = uiState.selectedCampaign?.id
            )

            result.onSuccess { orderId ->
                CartManager.clearCart()
                uiState = uiState.copy(isLoading = false)
                _events.emit(CheckoutEvent.OrderSuccess(orderId))
            }.onFailure { error ->
                uiState = uiState.copy(isLoading = false, error = error.message)
            }
        }
    }

    /**
     * Yeni kart kaydetme (DEV MODE - Token sistemi sonra eklenecek)
     */
    fun saveCard(card: SavedCard, onSuccess: () -> Unit) {
        val userId = UserManager.currentUser?.id ?: return
        
        viewModelScope.launch {
            uiState = uiState.copy(isSavingCard = true)
            try {
                // Kart bilgilerini Firebase'e kaydet
                val cardWithUserId = card.copy(userId = userId)
                val docRef = firestore.collection("users")
                    .document(userId)
                    .collection("savedCards")
                    .add(cardWithUserId)
                
                // Kaydedilen kartın ID'sini al ve güncelle
                val savedCardWithId = cardWithUserId.copy(id = docRef.id)
                
                // Eğer varsayılan kart olarak işaretlendiyse, diğer kartların varsayılan durumunu kaldır
                if (card.isDefault) {
                    val existingCards = firestore.collection("users")
                        .document(userId)
                        .collection("savedCards")
                        .get()
                    
                    existingCards.documents.forEach { doc ->
                        if (doc.id != docRef.id) {
                            firestore.collection("users")
                                .document(userId)
                                .collection("savedCards")
                                .document(doc.id)
                                .update("isDefault" to false)
                        }
                    }
                }
                
                // UserManager'ı güncelle
                UserManager.addSavedCard(savedCardWithId)
                
                uiState = uiState.copy(isSavingCard = false)
                onSuccess()
                
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSavingCard = false,
                    error = "Kart kaydedilemedi: ${e.message}"
                )
            }
        }
    }

    /**
     * Kullanıcının kayıtlı kartlarını yükle
     */
    fun loadSavedCards() {
        val userId = UserManager.currentUser?.id ?: return
        
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("users")
                    .document(userId)
                    .collection("savedCards")
                    .get()
                
                val cards = snapshot.documents.map { doc ->
                    doc.data<SavedCard>().copy(id = doc.id)
                }
                
                UserManager.updateSavedCards(cards)
            } catch (e: Exception) {
                // Hata durumunda sessizce devam et
            }
        }
    }

    /**
     * QR doğrulama gerekli mi kontrol et
     * NEW kullanıcı + Kasada ödeme = QR doğrulama gerekli
     */
    fun requiresQRVerification(): Boolean {
        val trustLevel = UserManager.trustLevel
        val isPayAtCounter = uiState.selectedPaymentMethodIndex == 1 // Kasada Öde
        val totalAmount = CartManager.totalPrice - uiState.discountAmount
        
        // Sepet 0 TL ise doğrulama gereksiz (zaten para alınmıyor)
        if (totalAmount <= 0) return false
        
        // NEW kullanıcı + Kasada ödeme = QR doğrulama gerekli
        return trustLevel == TrustLevel.NEW && isPayAtCounter && !uiState.isQRVerified
    }

    /**
     * QR kodu doğrula
     * @param scannedTableId Taranan QR'dan gelen masa ID
     * @return Doğrulama başarılı mı?
     */
    fun verifyQRCode(scannedTableId: String): Boolean {
        val expectedTableId = TenantSession.tableId
        
        if (scannedTableId == expectedTableId) {
            uiState = uiState.copy(isQRVerified = true)
            return true
        }
        
        uiState = uiState.copy(error = "Masa eşleşmiyor! Lütfen sipariş verdiğiniz masanın QR kodunu okutun.")
        return false
    }

    /**
     * QR doğrulama durumunu sıfırla
     */
    fun resetQRVerification() {
        uiState = uiState.copy(isQRVerified = false)
    }

    /**
     * Hata mesajını temizle
     */
    fun clearError() {
        uiState = uiState.copy(error = null)
    }
}

data class CheckoutUiState(
    val orderNote: String = "",
    val selectedPaymentMethodIndex: Int = 0,
    val isLoading: Boolean = false,
    val isSavingCard: Boolean = false,
    val error: String? = null,
    
    val availableCampaigns: List<Campaign> = emptyList(),
    val otherCampaigns: List<Pair<Campaign, String>> = emptyList(),
    val campaignDiscounts: Map<String, Double> = emptyMap(),
    val selectedCampaign: Campaign? = null,
    val isCampaignsLoading: Boolean = false,
    val appliedPromoCode: String? = null,
    val isPromoCodeLoading: Boolean = false,
    val promoCodeError: String? = null,
    val discountAmount: Double = 0.0,
    
    // QR Doğrulama
    val isQRVerified: Boolean = false
)

sealed interface CheckoutEvent {
    data class OrderSuccess(val orderId: String) : CheckoutEvent
}

