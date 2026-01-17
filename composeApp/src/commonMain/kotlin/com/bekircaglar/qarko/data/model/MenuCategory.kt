package com.bekircaglar.qarko.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MenuCategory(
    val id: String = "",
    val name: String = "",
    val emoji: String = "",
    val sortOrder: Int = 0,
    val isActive: Boolean = true,
    val itemCount: Int = 0,
    val suggestedPairingCategoryIds: List<String> = emptyList(), // Kategori bazlı öneriler
    // Zaman alanlarını serialization hatası almamak için şimdilik String? yapıyoruz
    val createdAt: String? = null,
    val updatedAt: String? = null
)
