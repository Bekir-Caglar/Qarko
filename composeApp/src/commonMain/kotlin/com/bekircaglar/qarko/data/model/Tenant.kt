package com.bekircaglar.qarko.data.model

import kotlinx.serialization.Serializable

/**
 * Tenant (İşletme) ana modeli
 */
@Serializable
data class Tenant(
    val id: String = "",
    val name: String = "",
    val slug: String = "", // URL dostu isim (örn: beko-yeri)
    val logo: String = "",
    val coverImage: String = "",
    val description: String = "",
    val contact: TenantContact = TenantContact(),
    val address: TenantAddress = TenantAddress(),
    val workingHours: WorkingHours = WorkingHours(),
    val features: TenantFeatures = TenantFeatures(),
    val status: TenantStatus = TenantStatus.ACTIVE,
    val isOpen: Boolean = true,
    val theme: TenantTheme = TenantTheme()
)

/**
 * İşletme iletişim bilgileri
 */
@Serializable
data class TenantContact(
    val phone: String = "",
    val email: String = "",
    val website: String = ""
)

/**
 * İşletme adres bilgileri
 */
@Serializable
data class TenantAddress(
    val street: String = "",
    val city: String = "",
    val district: String = "",
    val country: String = "Türkiye",
    val postalCode: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

/**
 * Çalışma saatleri
 */
@Serializable
data class WorkingHours(
    val monday: DayHours = DayHours(),
    val tuesday: DayHours = DayHours(),
    val wednesday: DayHours = DayHours(),
    val thursday: DayHours = DayHours(),
    val friday: DayHours = DayHours(),
    val saturday: DayHours = DayHours(),
    val sunday: DayHours = DayHours()
)

@Serializable
data class DayHours(
    val isOpen: Boolean = true,
    val openTime: String = "09:00",
    val closeTime: String = "22:00"
)

/**
 * İşletme özellikleri
 */
@Serializable
data class TenantFeatures(
    val hasAlcohol: Boolean = false,
    val hasHookah: Boolean = false,
    val hasDelivery: Boolean = false,
    val hasTakeaway: Boolean = true,
    val hasDineIn: Boolean = true,
    val hasWifi: Boolean = false,
    val hasParking: Boolean = false,
    val acceptsCreditCard: Boolean = true,
    val acceptsCash: Boolean = true,
    val isHalal: Boolean = false,
    val hasOutdoorSeating: Boolean = false,
    val hasLiveMusic: Boolean = false,
    val isChildFriendly: Boolean = true,
    val isPetFriendly: Boolean = false
)

/**
 * İşletme durumu
 */
@Serializable
enum class TenantStatus {
    ACTIVE,     // Aktif
    INACTIVE,   // Pasif
    SUSPENDED,  // Askıya alınmış
    PENDING     // Onay bekliyor
}

/**
 * İşletme tema ayarları
 */
@Serializable
data class TenantTheme(
    val primaryColor: String = "#E23744", // Hex renk kodu
    val secondaryColor: String = "#2B2D42",
    val accentColor: String = "#FFA726"
)

