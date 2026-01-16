package com.bekircaglar.qarko.data.model

import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Tenant(
    val id: String = "",
    val name: String = "",
    val slug: String = "",
    val logo: String = "",
    val coverImage: String = "",
    val description: String = "",
    val contact: TenantContact = TenantContact(),
    val address: TenantAddress = TenantAddress(),
    val workingHours: WorkingHours = WorkingHours(),
    val features: TenantFeatures = TenantFeatures(),
    val status: String = "ACTIVE",
    val isOpen: Boolean = true,
    val onboardingCompleted: Boolean = false,
    // Zaman alanlarını serialization hatası almamak için şimdilik opsiyonel bırakıyoruz
    // Firestore Timestamp tipi direkt Instant'a cast edilemeyebilir
    val createdAt: String? = null, 
    val updatedAt: String? = null
)

@Serializable
data class TenantContact(
    val phone: String = "",
    val email: String = "",
    val website: String? = null
)

@Serializable
data class TenantAddress(
    val street: String = "",
    val district: String = "",
    val city: String = "",
    val country: String = "Türkiye"
)

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
    val open: String = "09:00",
    val close: String = "22:00",
    val isOpen: Boolean = true
) {
    fun formattedHours(): String? = if (!isOpen) "Kapalı" else "$open - $close"
    fun isOpenNow(): Boolean = isOpen // Basit versiyon
}

fun WorkingHours.forToday(
    clock: Clock = Clock.System,
    zone: TimeZone = TimeZone.currentSystemDefault()
): DayHours {
    return when (clock.now().toLocalDateTime(zone).dayOfWeek) {
        DayOfWeek.MONDAY -> monday
        DayOfWeek.TUESDAY -> tuesday
        DayOfWeek.WEDNESDAY -> wednesday
        DayOfWeek.THURSDAY -> thursday
        DayOfWeek.FRIDAY -> friday
        DayOfWeek.SATURDAY -> saturday
        DayOfWeek.SUNDAY -> sunday
        else -> monday
    }
}

@Serializable
data class TenantFeatures(
    val hasAlcohol: Boolean = false,
    val hasHookah: Boolean = false,
    val hasPizza: Boolean = false,
    val hasBurger: Boolean = false,
    val hasDelivery: Boolean = false,
    val hasTakeaway: Boolean = true,
    val hasDineIn: Boolean = true
)
