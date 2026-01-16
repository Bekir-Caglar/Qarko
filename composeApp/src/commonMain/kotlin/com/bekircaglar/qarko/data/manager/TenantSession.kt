package com.bekircaglar.qarko.data.manager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.bekircaglar.qarko.data.model.MenuCategory
import com.bekircaglar.qarko.data.model.QRScanResult
import com.bekircaglar.qarko.data.model.Table
import com.bekircaglar.qarko.data.model.Tenant

/**
 * Aktif tenant (işletme) oturumunu yöneten singleton manager
 * QR kod tarandığında tenant ve masa bilgilerini saklar
 */
object TenantSession {

    /**
     * Aktif tenant bilgisi
     */
    var currentTenant: Tenant? by mutableStateOf(null)
        private set

    /**
     * Aktif masa bilgisi
     */
    var currentTable: Table? by mutableStateOf(null)
        private set

    /**
     * QR tarama sonucu
     */
    var scanResult: QRScanResult? by mutableStateOf(null)
        private set

    /**
     * Tenant'ın kategorileri
     */
    var categories: List<MenuCategory> by mutableStateOf(emptyList())
        private set

    /**
     * Oturum başlatıldı mı?
     */
    val isSessionActive: Boolean
        get() = currentTenant != null && scanResult?.isValid == true

    /**
     * Tenant ID
     */
    val tenantId: String?
        get() = currentTenant?.id

    /**
     * Tenant slug
     */
    val tenantSlug: String?
        get() = currentTenant?.slug ?: scanResult?.tenantSlug

    /**
     * Table ID
     */
    val tableId: String?
        get() = currentTable?.id ?: scanResult?.tableId

    /**
     * QR kod tarandığında çağrılır
     */
    fun updateScanResult(result: QRScanResult) {
        scanResult = result
    }

    /**
     * Tenant bilgisini set eder
     */
    fun updateTenant(tenant: Tenant) {
        currentTenant = tenant
    }

    /**
     * Masa bilgisini set eder
     */
    fun updateTable(table: Table?) {
        currentTable = table
    }

    /**
     * Kategorileri set eder
     */
    fun updateCategories(categoryList: List<MenuCategory>) {
        categories = categoryList.sortedBy { it.sortOrder }
    }

    /**
     * Oturum başlatır (tenant ve masa ile birlikte)
     */
    fun startSession(
        scanResult: QRScanResult,
        tenant: Tenant,
        table: Table? = null,
        categories: List<MenuCategory> = emptyList()
    ) {
        this.scanResult = scanResult
        this.currentTenant = tenant
        this.currentTable = table
        this.categories = categories.sortedBy { it.sortOrder }
    }

    /**
     * Oturumu sonlandırır ve tüm verileri temizler
     */
    fun clearSession() {
        currentTenant = null
        currentTable = null
        scanResult = null
        categories = emptyList()
    }

    /**
     * İşletmenin belirli bir özelliği var mı kontrol eder
     */
    fun hasFeature(feature: TenantFeature): Boolean {
        return when (feature) {
            TenantFeature.ALCOHOL -> currentTenant?.features?.hasAlcohol == true
            TenantFeature.HOOKAH -> currentTenant?.features?.hasHookah == true
            TenantFeature.DELIVERY -> currentTenant?.features?.hasDelivery == true
            TenantFeature.TAKEAWAY -> currentTenant?.features?.hasTakeaway == true
            TenantFeature.DINE_IN -> currentTenant?.features?.hasDineIn == true

            else -> {
                false
            }
        }

    }
}

/**
 * İşletme özellikleri enum
 */
enum class TenantFeature {
    ALCOHOL,
    HOOKAH,
    DELIVERY,
    TAKEAWAY,
    DINE_IN,
    WIFI,
    PARKING,
    CREDIT_CARD,
    CASH,
    HALAL
}

