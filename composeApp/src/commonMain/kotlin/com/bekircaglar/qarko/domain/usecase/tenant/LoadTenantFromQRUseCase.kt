package com.bekircaglar.qarko.domain.usecase.tenant

import com.bekircaglar.qarko.data.manager.HistoryManager
import com.bekircaglar.qarko.data.manager.TenantSession
import com.bekircaglar.qarko.data.model.QRScanResult
import com.bekircaglar.qarko.domain.repository.ITenantRepository

/**
 * QR kod tarandığında tenant ve masa bilgilerini yükleyen use case
 */
class LoadTenantFromQRUseCase(
    private val tenantRepository: ITenantRepository
) {

    /**
     * QR kod URL'inden tenant bilgilerini yükler ve session'ı başlatır
     * @param qrUrl QR koddan okunan URL
     * @return İşlem sonucu
     */
    suspend operator fun invoke(qrUrl: String): Result<TenantLoadResult> {
        // 1. QR URL'i parse et
        val scanResult = QRScanResult.fromUrl(qrUrl)

        if (!scanResult.isValid) {
            return Result.failure(
                QRParseException(scanResult.errorMessage ?: "Geçersiz QR kod")
            )
        }

        // 2. Tenant bilgisini al
        val tenantResult = tenantRepository.getTenantBySlug(scanResult.tenantSlug)
        val tenant = tenantResult.getOrNull()
            ?: return Result.failure(
                TenantNotFoundException("İşletme bulunamadı: ${scanResult.tenantSlug}")
            )

        // 3. Kategorileri al
        val categoriesResult = tenantRepository.getCategories(tenant.id)
        val categories = categoriesResult.getOrElse { emptyList() }

        // 4. Masa bilgisini al (varsa)
        val table = scanResult.tableId?.let { tableId ->
            tenantRepository.getTable(tenant.id, tableId).getOrNull()
        }

        // 5. Session'ı başlat
        TenantSession.startSession(
            scanResult = scanResult,
            tenant = tenant,
            table = table,
            categories = categories
        )

        // 6. Geçmişe ekle
        HistoryManager.addVisitedMenu(tenant, table?.id)

        return Result.success(
            TenantLoadResult(
                tenant = tenant,
                categories = categories,
                tableId = table?.id,
                tableName = table?.name
            )
        )
    }

    /**
     * URL yerine direkt tenantId ve tableId ile yükleme
     */
    suspend fun loadDirect(tenantSlug: String, tableId: String? = null): Result<TenantLoadResult> {
        val url = buildString {
            append("https://qarko.app/menu/")
            append(tenantSlug)
            if (tableId != null) {
                append("?tableId=")
                append(tableId)
            }
        }
        return invoke(url)
    }
}

/**
 * Tenant yükleme sonucu
 */
data class TenantLoadResult(
    val tenant: com.bekircaglar.qarko.data.model.Tenant,
    val categories: List<com.bekircaglar.qarko.data.model.MenuCategory>,
    val tableId: String?,
    val tableName: String?
)

/**
 * QR parse hatası
 */
class QRParseException(message: String) : Exception(message)

/**
 * Tenant bulunamadı hatası
 */
class TenantNotFoundException(message: String) : Exception(message)
