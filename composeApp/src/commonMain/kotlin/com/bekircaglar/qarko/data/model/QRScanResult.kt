package com.bekircaglar.qarko.data.model

import kotlinx.serialization.Serializable

/**
 * QR kod taraması sonucu parse edilen veri
 * URL formatı: https://qarko.app/menu/{tenantSlug}?tableId={tableId}
 */
@Serializable
data class QRScanResult(
    val tenantSlug: String, // İşletme slug'ı (örn: beko-yeri)
    val tableId: String? = null, // Masa ID'si (opsiyonel)
    val isValid: Boolean = true,
    val errorMessage: String? = null,
    val scannedAt: Long = 0L // Tarama zamanı (epoch millis)
) {
    companion object {
        /**
         * QR koddan okunan URL'i parse eder
         * @param url QR koddan okunan URL (örn: https://qarko.app/menu/beko-yeri?tableId=ic4Db9JrLcbhkHXrAJYM)
         * @return QRScanResult
         */
        fun fromUrl(url: String): QRScanResult {
            val currentTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            
            return try {
                // URL pattern: https://qarko.app/menu/{tenantSlug}?tableId={tableId}
                val regex = Regex("""https?://(?:www\.)?qarko\.app/menu/([a-zA-Z0-9_-]+)(?:\?tableId=([a-zA-Z0-9_-]+))?""")
                val matchResult = regex.find(url)

                if (matchResult != null) {
                    val (tenantSlug, tableId) = matchResult.destructured
                    QRScanResult(
                        tenantSlug = tenantSlug,
                        tableId = tableId.takeIf { it.isNotEmpty() },
                        isValid = true,
                        scannedAt = currentTime
                    )
                } else {
                    // Alternatif basit parse
                    val cleanUrl = url.removePrefix("https://").removePrefix("http://")
                        .removePrefix("www.").removePrefix("qarko.app/menu/")

                    val parts = cleanUrl.split("?")
                    val slug = parts.firstOrNull()?.takeIf { it.isNotEmpty() }

                    if (slug != null) {
                        val tableId = if (parts.size > 1) {
                            parts[1].removePrefix("tableId=").takeIf { it.isNotEmpty() }
                        } else null

                        QRScanResult(
                            tenantSlug = slug,
                            tableId = tableId,
                            isValid = true,
                            scannedAt = currentTime
                        )
                    } else {
                        QRScanResult(
                            tenantSlug = "",
                            isValid = false,
                            errorMessage = "Geçersiz QR kod formatı",
                            scannedAt = currentTime
                        )
                    }
                }
            } catch (e: Exception) {
                QRScanResult(
                    tenantSlug = "",
                    isValid = false,
                    errorMessage = "QR kod okunamadı: ${e.message}",
                    scannedAt = currentTime
                )
            }
        }
    }
}

