package com.bekircaglar.qarko.data.repository

import com.bekircaglar.qarko.data.model.Campaign
import com.bekircaglar.qarko.domain.repository.ICampaignRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CampaignRepository : ICampaignRepository {

    private val firestore = Firebase.firestore
    private val tenantsCollection = firestore.collection("tenants")

    override suspend fun getActiveCampaigns(tenantId: String): Result<List<Campaign>> {
        return try {
            val now = Timestamp.now()
            val snapshot = tenantsCollection
                .document(tenantId)
                .collection("campaigns")
                .where("validity.isActive", equalTo = true)
                .get()

            val campaigns = snapshot.documents.map { doc ->
                val data = doc.data<Campaign>()
                if (data.id.isEmpty()) data.copy(id = doc.id) else data
            }.filter { campaign ->
                val start = campaign.validity.startDate
                val end = campaign.validity.endDate
                
                val isAfterStart = start?.let { 
                    it.seconds < now.seconds || (it.seconds == now.seconds && it.nanoseconds <= now.nanoseconds)
                } ?: true
                
                val isBeforeEnd = end?.let {
                    it.seconds > now.seconds || (it.seconds == now.seconds && it.nanoseconds >= now.nanoseconds)
                } ?: true

                isAfterStart && isBeforeEnd
            }

            Result.success(campaigns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getActiveCampaignsFlow(tenantId: String): Flow<List<Campaign>> {
        return tenantsCollection
            .document(tenantId)
            .collection("campaigns")
            .where("validity.isActive", equalTo = true)
            .snapshots
            .map { snapshot ->
                val now = Timestamp.now()
                snapshot.documents.map { doc ->
                    val data = doc.data<Campaign>()
                    if (data.id.isEmpty()) data.copy(id = doc.id) else data
                }.filter { campaign ->
                    val start = campaign.validity.startDate
                    val end = campaign.validity.endDate
                    
                    val isAfterStart = start?.let { 
                        it.seconds < now.seconds || (it.seconds == now.seconds && it.nanoseconds <= now.nanoseconds)
                    } ?: true
                    
                    val isBeforeEnd = end?.let {
                        it.seconds > now.seconds || (it.seconds == now.seconds && it.nanoseconds >= now.nanoseconds)
                    } ?: true

                    isAfterStart && isBeforeEnd
                }
            }
    }
}
