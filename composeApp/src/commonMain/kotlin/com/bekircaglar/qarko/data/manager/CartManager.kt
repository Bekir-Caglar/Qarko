package com.bekircaglar.qarko.data.manager

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bekircaglar.qarko.data.model.CartItemData
import com.bekircaglar.qarko.data.model.FoodItem
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Global Cart Manager - Singleton pattern for managing cart state across the app
 */
object CartManager {

    // Observable cart items list
    private val _cartItems = mutableStateListOf<CartItemData>()
    val cartItems: SnapshotStateList<CartItemData> = _cartItems

    // Total item count in cart
    val totalItemCount: Int
        get() = _cartItems.sumOf { it.quantity }

    // Total price of all items in cart
    val totalPrice: Double
        get() = _cartItems.sumOf { it.price * it.quantity }

    /**
     * Add item to cart from FoodDetailScreen
     */
    @OptIn(ExperimentalUuidApi::class)
    fun addToCart(
        foodItem: FoodItem,
        quantity: Int,
        selectedSingleOptions: Map<String, String>,
        selectedMultiOptions: Map<String, Set<String>>,
        removedItems: Set<String>,
        totalPrice: Double
    ) {
        // Generate unique ID for this cart item
        val cartItemId = Uuid.random().toString()

        // Build description from selected options
        val descriptionParts = mutableListOf<String>()

        // Add single select options to description
        foodItem.customizationGroups.forEach { group ->
            val selectedOptionId = selectedSingleOptions[group.id]
            val selectedOption = group.options.find { it.id == selectedOptionId }
            if (selectedOption != null && selectedOption.name.isNotEmpty()) {
                descriptionParts.add(selectedOption.name)
            }
        }

        // Add multi select options to description
        foodItem.customizationGroups.forEach { group ->
            val selectedIds = selectedMultiOptions[group.id] ?: emptySet()
            val selectedNames = group.options.filter { it.id in selectedIds }.map { it.name }
            if (selectedNames.isNotEmpty()) {
                descriptionParts.addAll(selectedNames)
            }
        }

        // Add removed items to description
        val removedNames = foodItem.removableItems.filter { it.id in removedItems }.map { "- ${it.name}" }
        if (removedNames.isNotEmpty()) {
            descriptionParts.addAll(removedNames)
        }

        val description = if (descriptionParts.isNotEmpty()) {
            descriptionParts.joinToString(", ")
        } else {
            foodItem.info
        }

        val cartItem = CartItemData(
            id = cartItemId,
            foodId = foodItem.id,
            imageUrl = foodItem.imageUrl,
            name = foodItem.name,
            description = description,
            basePrice = foodItem.price.replace("₺", "").replace(",", ".").toDoubleOrNull() ?: 0.0,
            price = totalPrice / quantity, // Price per item including extras
            quantity = quantity,
            selectedOptions = selectedSingleOptions,
            selectedMultiOptions = selectedMultiOptions,
            removedItems = removedItems
        )

        _cartItems.add(cartItem)
    }

    /**
     * Update quantity of an item in cart
     */
    fun updateQuantity(cartItemId: String, newQuantity: Int) {
        val index = _cartItems.indexOfFirst { it.id == cartItemId }
        if (index != -1) {
            if (newQuantity <= 0) {
                _cartItems.removeAt(index)
            } else {
                _cartItems[index] = _cartItems[index].copy(quantity = newQuantity)
            }
        }
    }

    /**
     * Remove item from cart
     */
    fun removeFromCart(cartItemId: String) {
        _cartItems.removeAll { it.id == cartItemId }
    }

    /**
     * Clear all items from cart
     */
    fun clearCart() {
        _cartItems.clear()
    }

    /**
     * Get cart item by ID
     */
    fun getCartItem(cartItemId: String): CartItemData? {
        return _cartItems.find { it.id == cartItemId }
    }

    /**
     * Check if cart is empty
     */
    fun isEmpty(): Boolean = _cartItems.isEmpty()
}

