package com.bekircaglar.qarko.data.model

import kotlinx.serialization.Serializable

/**
 * Ana ürün sınıfı - tüm yemek türlerini destekler
 */
@Serializable
data class FoodItem(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val price: String = "₺0", // İndirimli veya normal fiyat (gösterilecek ana fiyat)
    val originalPrice: String? = null, // İndirim varsa orijinal fiyat (üstü çizili gösterilecek)
    val info: String = "",
    val category: String = "",
    val rating: Float = 0f,
    val ratingCount: Int = 0,
    val ingredients: List<Ingredient> = emptyList(),
    val allergens: List<Allergen> = emptyList(),
    val customizationGroups: List<CustomizationGroup> = emptyList(),
    val removableItems: List<RemovableItem> = emptyList(),
    val foodType: FoodType = FoodType.OTHER,
    val isNew: Boolean = false,
    val isFeatured: Boolean = false,
    val discountPercent: Int = 0,
    val prepTime: String? = null,
    val calories: Int = 0,
    val suggestedPairingCategoryIds: List<String> = emptyList() // "Yanında İyi Gider" kategori ID'leri
)

/**
 * Yemek türleri - her tür için farklı özelleştirme seçenekleri
 */
@Serializable
enum class FoodType {
    PIZZA,          // Boy, hamur tipi
    BURGER,         // Et pişirme derecesi, ekstralar, çıkarılabilir malzemeler
    DRINK,          // Boyut, buz, şeker (alkolsüz)
    ALCOHOLIC,      // Alkollü içecekler - bira, şarap, kokteyl
    HOT_DRINK,      // Sıcak içecekler - kahve, çay
    SALAD,          // Sos seçimi, ekstra malzemeler
    DESSERT,        // Boyut, ekstra sos
    STARTER,        // Porsiyon boyutu
    SIDE,           // Boyut, sos
    KEBAB,          // İskender, döner vb. - porsiyon, ekmek tipi, sos
    MAIN_COURSE,    // Ana yemekler - porsiyon, pişirme
    SOUP,           // Çorbalar - porsiyon
    PASTA,          // Makarnalar - boyut, sos, peynir
    SANDWICH,       // Sandviçler - ekmek tipi, ekstralar
    WRAP,           // Dürümler - boyut, sos
    SEAFOOD,        // Deniz ürünleri - porsiyon, pişirme
    BREAKFAST,      // Kahvaltı - porsiyon, ekstralar
    HOOKAH,         // Nargile - aroma, kömür tipi
    SNACK,          // Atıştırmalıklar
    ICE_CREAM,      // Dondurma - boyut, çeşit
    WAFFLE,         // Waffle - sos, topping
    OTHER
}

/**
 * İçerik/malzeme bilgisi
 */
@Serializable
data class Ingredient(
    val id: String = "",
    val name: String = "",
    val iconName: String = "", // İkon adı (örn: "tomato", "cheese", "pepper")
    val isMain: Boolean = false // Ana malzeme mi?
)

/**
 * Özelleştirme grubu - Pizza boyutu, Hamur tipi, İçecek boyutu vb.
 */
@Serializable
data class CustomizationGroup(
    val id: String = "",
    val name: String = "", // Örn: "Boy", "Hamur Tipi", "Et Pişirme"
    val type: CustomizationType = CustomizationType.SINGLE_SELECT,
    val isRequired: Boolean = false,
    val options: List<CustomizationOption> = emptyList()
)

/**
 * Özelleştirme seçenek tipi
 */
@Serializable
enum class CustomizationType {
    SINGLE_SELECT,  // Sadece bir seçim (Boy, Hamur)
    MULTI_SELECT,   // Birden fazla seçim (Ekstra malzemeler)
    QUANTITY        // Miktar seçimi
}

/**
 * Özelleştirme seçeneği
 */
@Serializable
data class CustomizationOption(
    val id: String = "",
    val name: String = "",
    val extraPrice: String = "₺0", // Ekstra fiyat
    val isDefault: Boolean = false
)

/**
 * Çıkarılabilir malzeme (Burger için soğan çıkar, turşu çıkar vb.)
 */
@Serializable
data class RemovableItem(
    val id: String = "",
    val name: String = "",
    val emoji: String = "",
    val isRemoved: Boolean = false
)

/**
 * Alerjen bilgisi
 */
@Serializable
enum class Allergen(val displayName: String, val iconName: String) {
    GLUTEN("Gluten", "wheat"),
    DAIRY("Süt Ürünleri", "milk"),
    EGGS("Yumurta", "egg"),
    NUTS("Kuruyemiş", "nut"),
    SOY("Soya", "soybean"),
    FISH("Balık", "fish"),
    SHELLFISH("Kabuklu Deniz Ürünleri", "shellfish"),
    SESAME("Susam", "sesame")
}
