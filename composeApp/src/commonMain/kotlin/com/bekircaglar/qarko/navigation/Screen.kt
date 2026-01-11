package com.bekircaglar.qarko.navigation

import com.bekircaglar.qarko.data.model.Allergen
import com.bekircaglar.qarko.data.model.CustomizationGroup
import com.bekircaglar.qarko.data.model.FoodItem
import com.bekircaglar.qarko.data.model.FoodType
import com.bekircaglar.qarko.data.model.Ingredient
import com.bekircaglar.qarko.data.model.RemovableItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val navJson = Json { ignoreUnknownKeys = true }

/**
 * Navigation route tanımlamaları - Type-safe navigation ile
 */

@Serializable
object Welcome

@Serializable
object QRScan

@Serializable
object TenantMenu

@Serializable
data class FoodDetail(
    val id: String,
    val name: String,
    val imageUrl: String,
    val price: String,
    val info: String,
    val category: String = "",
    val rating: Float = 0f,
    val ratingCount: Int = 0,
    val foodTypeOrdinal: Int = 0,
    val ingredientsJson: String = "[]",
    val customizationGroupsJson: String = "[]",
    val removableItemsJson: String = "[]",
    val allergensJson: String = "[]"
) {
    companion object {
        fun fromFoodItem(foodItem: FoodItem): FoodDetail {
            return FoodDetail(
                id = foodItem.id,
                name = foodItem.name,
                imageUrl = foodItem.imageUrl,
                price = foodItem.price,
                info = foodItem.info,
                category = foodItem.category,
                rating = foodItem.rating,
                ratingCount = foodItem.ratingCount,
                foodTypeOrdinal = foodItem.foodType.ordinal,
                ingredientsJson = navJson.encodeToString(foodItem.ingredients),
                customizationGroupsJson = navJson.encodeToString(foodItem.customizationGroups),
                removableItemsJson = navJson.encodeToString(foodItem.removableItems),
                allergensJson = navJson.encodeToString(foodItem.allergens)
            )
        }
    }

    fun toFoodItem(): FoodItem {
        return FoodItem(
            id = id,
            name = name,
            imageUrl = imageUrl,
            price = price,
            info = info,
            category = category,
            rating = rating,
            ratingCount = ratingCount,
            foodType = FoodType.entries.getOrElse(foodTypeOrdinal) { FoodType.OTHER },
            ingredients = try { navJson.decodeFromString<List<Ingredient>>(ingredientsJson) } catch (e: Exception) { emptyList() },
            customizationGroups = try { navJson.decodeFromString<List<CustomizationGroup>>(customizationGroupsJson) } catch (e: Exception) { emptyList() },
            removableItems = try { navJson.decodeFromString<List<RemovableItem>>(removableItemsJson) } catch (e: Exception) { emptyList() },
            allergens = try { navJson.decodeFromString<List<Allergen>>(allergensJson) } catch (e: Exception) { emptyList() }
        )
    }
}

@Serializable
object Cart

@Serializable
object Checkout

@Serializable
object Orders

@Serializable
object Campaign

@Serializable
object CampaignSelect

@Serializable
object Profile

@Serializable
object Auth

@Serializable
object Login

@Serializable
object Register

@Serializable
object Otp

// Eski string route'lar için backwards compatibility
object Screens {
    const val WELCOME = "welcome"
    const val QR_SCAN = "qr_scan"
    const val TENANT_MENU = "tenant_menu"
    const val FOOD_DETAIL = "food_detail"
    const val CART = "cart"
    const val ORDERS = "orders"
    const val CAMPAIGN = "campaign"
    const val CAMPAIGN_SELECT = "campaign_select"
    const val PROFILE = "profile"
    const val AUTH = "auth"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val OTP = "otp"
}
