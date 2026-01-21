package com.bekircaglar.qarko.domain.repository

import com.bekircaglar.qarko.data.model.Campaign
import kotlinx.coroutines.flow.Flow

interface ICampaignRepository {
    suspend fun getActiveCampaigns(tenantId: String): Result<List<Campaign>>
    fun getActiveCampaignsFlow(tenantId: String): Flow<List<Campaign>>
}
