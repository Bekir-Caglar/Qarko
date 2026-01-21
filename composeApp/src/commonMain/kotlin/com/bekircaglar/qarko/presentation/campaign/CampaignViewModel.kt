package com.bekircaglar.qarko.presentation.campaign

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.qarko.data.manager.TenantSession
import com.bekircaglar.qarko.data.model.Campaign
import com.bekircaglar.qarko.domain.repository.ICampaignRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CampaignViewModel(
    private val campaignRepository: ICampaignRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CampaignUiState>(CampaignUiState.Loading)
    val uiState: StateFlow<CampaignUiState> = _uiState.asStateFlow()

    init {
        loadCampaigns()
    }

    private fun loadCampaigns() {
        val tenantId = TenantSession.tenantId
        if (tenantId == null) {
            _uiState.value = CampaignUiState.Error("İşletme bilgisi bulunamadı.")
            return
        }

        viewModelScope.launch {
            campaignRepository.getActiveCampaignsFlow(tenantId).collect { campaigns ->
                // Kampanya kodu gerektirmeyen kampanyaları filtrele
                val filteredCampaigns = campaigns.filter { it.conditions.requiresCode != true }
                
                if (filteredCampaigns.isEmpty()) {
                    _uiState.value = CampaignUiState.Empty
                } else {
                    _uiState.value = CampaignUiState.Success(filteredCampaigns)
                }
            }
        }
    }
}

sealed interface CampaignUiState {
    data object Loading : CampaignUiState
    data class Success(val campaigns: List<Campaign>) : CampaignUiState
    data class Error(val message: String) : CampaignUiState
    data object Empty : CampaignUiState
}
