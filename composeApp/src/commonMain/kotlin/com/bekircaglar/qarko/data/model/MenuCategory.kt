package com.bekircaglar.qarko.data.model

import kotlinx.serialization.Serializable

/**
 * Menü kategorisi modeli
 * Firebase path: tenants/{tenantId}/categories/{categoryId}
 */
@Serializable
data class MenuCategory(
    val id: String = "",
    val name: String = "",
    val emoji: String = "", // Kategori ikonu (örn: 🍕, 🍔, 🥗)
    val sortOrder: Int = 0,
    val isActive: Boolean = true,
    val itemCount: Int = 0,
    val description: String = ""
)

