package com.bekircaglar.qarko.presentation.checkout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.qarko.data.manager.CartManager
import com.bekircaglar.qarko.data.manager.TenantSession
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

    private fun loadCampaigns() {
        val tenantId = TenantSession.tenantId ?: return
        viewModelScope.launch {
            uiState = uiState.copy(isCampaignsLoading = true)
            try {
                val snapshot = firestore.collection("tenants")
                    .document(tenantId)
                    .collection("campaigns")
                    .get()

                val campaigns = snapshot.documents.map { it.data<Campaign>().copy(id = it.id) }
                validateCampaigns(campaigns)
            } catch (e: Exception) {
                uiState = uiState.copy(isCampaignsLoading = false)
            }
        }
    }

    private fun validateCampaigns(campaigns: List<Campaign>) {
        val cartTotal = CartManager.totalPrice
        val now = Clock.System.now()
        val currentDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val currentDay = currentDateTime.dayOfWeek.ordinal // Adjust if 0-6 Sunday=0 is needed
        val currentTimeStr = "${currentDateTime.hour.toString().padStart(2, '0')}:${currentDateTime.minute.toString().padStart(2, '0')}"

        val available = mutableListOf<Campaign>()
        val others = mutableListOf<Pair<Campaign, String>>()

        campaigns.filter { it.validity.isActive }.forEach { campaign ->
            val conditions = campaign.conditions
            val validity = campaign.validity

            // Date validation
            if (validity.startDate != null && now < validity.startDate) return@forEach
            if (validity.endDate != null && now > validity.endDate) return@forEach

            // Promo code validation (skip auto-listing if it requires code and user hasn't entered it)
            if (conditions.requiresCode == true && uiState.appliedPromoCode != conditions.code) {
                return@forEach
            }

            // Min Amount validation
            if (conditions.minOrderAmount != null && cartTotal < conditions.minOrderAmount) {
                others.add(campaign to "Harcanması gereken ek tutar: ₺${(conditions.minOrderAmount - cartTotal).toInt()}")
                return@forEach
            }

            // Day validation
            if (conditions.applicableDays != null && currentDay !in conditions.applicableDays) {
                others.add(campaign to "Bu kampanya bugün geçerli değil.")
                return@forEach
            }

            // Hour validation
            if (conditions.applicableHours != null) {
                if (currentTimeStr < conditions.applicableHours.start || currentTimeStr > conditions.applicableHours.end) {
                    others.add(campaign to "Bu kampanya şu an geçerli değil (${conditions.applicableHours.start} - ${conditions.applicableHours.end})")
                    return@forEach
                }
            }

            available.add(campaign)
        }

        uiState = uiState.copy(
            availableCampaigns = available,
            otherCampaigns = others,
            isCampaignsLoading = false
        )
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

                if (snapshot.documents.isNotEmpty()) {
                    val campaign = snapshot.documents.first().data<Campaign>().copy(id = snapshot.documents.first().id)
                    uiState = uiState.copy(appliedPromoCode = code)
                    loadCampaigns() // Re-validate with new code
                    uiState = uiState.copy(promoCodeError = null)
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
            return
        }

        val cartTotal = CartManager.totalPrice
        val discount = when (campaign.type) {
            CampaignType.PERCENTAGE_DISCOUNT -> {
                val amount = cartTotal * (campaign.discountValue / 100)
                campaign.conditions.maxDiscountAmount?.let { minOf(amount, it) } ?: amount
            }
            CampaignType.FIXED_DISCOUNT -> campaign.discountValue
            else -> 0.0
        }
        uiState = uiState.copy(discountAmount = discount)
    }

    fun onOrderNoteChange(note: String) {
        uiState = uiState.copy(orderNote = note)
    }

    fun onPaymentMethodChange(index: Int) {
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
                notes = uiState.orderNote
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
}

data class CheckoutUiState(
    val orderNote: String = "",
    val selectedPaymentMethodIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // Campaign related state
    val availableCampaigns: List<Campaign> = emptyList(),
    val otherCampaigns: List<Pair<Campaign, String>> = emptyList(),
    val selectedCampaign: Campaign? = null,
    val isCampaignsLoading: Boolean = false,
    val appliedPromoCode: String? = null,
    val isPromoCodeLoading: Boolean = false,
    val promoCodeError: String? = null,
    val discountAmount: Double = 0.0
)

sealed interface CheckoutEvent {
    data class OrderSuccess(val orderId: String) : CheckoutEvent
}
