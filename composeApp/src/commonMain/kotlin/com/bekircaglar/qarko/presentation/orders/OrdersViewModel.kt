package com.bekircaglar.qarko.presentation.orders

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekircaglar.qarko.data.manager.CartManager
import com.bekircaglar.qarko.data.manager.UserManager
import com.bekircaglar.qarko.data.model.Order
import com.bekircaglar.qarko.data.model.OrderItem
import com.bekircaglar.qarko.data.model.OrderStatus
import com.bekircaglar.qarko.domain.repository.IOrderRepository
import com.bekircaglar.qarko.domain.repository.ITenantRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Sipariş listesi filtre seçenekleri
 */
enum class OrderFilterType(val displayName: String) {
    ALL("Tümü"),
    ACTIVE("Aktif"),
    COMPLETED("Tamamlanan"),
    CANCELLED("İptal")
}

class OrdersViewModel(
    private val orderRepository: IOrderRepository,
    private val tenantRepository: ITenantRepository
) : ViewModel() {

    var uiState by mutableStateOf(OrdersUiState())
        private set

    // Tüm siparişlerin orijinal listesi (filtrelenmemiş)
    private var allOrders: List<Order> = emptyList()

    init {
        loadOrders()
    }

    fun loadOrders() {
        val userId = UserManager.currentUser?.id
        println("OrdersViewModel: loadOrders called, userId=$userId")

        if (userId == null) {
            println("OrdersViewModel: No user logged in!")
            uiState = uiState.copy(
                isLoading = false,
                error = "Lütfen giriş yapın"
            )
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            println("OrdersViewModel: Starting to observe orders for userId=$userId")
            orderRepository.observeUserOrders(userId)
                .catch { e ->
                    println("OrdersViewModel: Error - ${e.message}")
                    e.printStackTrace()
                    uiState = uiState.copy(
                        isLoading = false,
                        error = "Siparişler yüklenemedi: ${e.message}"
                    )
                }
                .collect { orders ->
                    println("OrdersViewModel: Received ${orders.size} orders")
                    orders.forEach { order ->
                        println("OrdersViewModel: Order - id=${order.id}, orderNumber=${order.orderNumber}, status=${order.status}")
                    }
                    allOrders = orders
                    uiState = uiState.copy(
                        isLoading = false,
                        orders = applyFilter(orders, uiState.selectedFilter),
                        error = null,
                        isRefreshing = false
                    )
                }
        }
    }

    /**
     * Filtre değiştiğinde çağrılır
     */
    fun setFilter(filter: OrderFilterType) {
        uiState = uiState.copy(
            selectedFilter = filter,
            orders = applyFilter(allOrders, filter)
        )
    }

    /**
     * Sipariş detayını görüntülemek için seçili siparişi ayarlar
     */
    fun selectOrder(order: Order?) {
        uiState = uiState.copy(selectedOrder = order)
    }

    /**
     * Pull-to-refresh için
     */
    fun refresh() {
        uiState = uiState.copy(isRefreshing = true)
        loadOrders()
    }

    /**
     * Filtreleme mantığı
     */
    private fun applyFilter(orders: List<Order>, filter: OrderFilterType): List<Order> {
        return when (filter) {
            OrderFilterType.ALL -> orders
            OrderFilterType.ACTIVE -> orders.filter { order ->
                order.status in listOf(
                    OrderStatus.PENDING,
                    OrderStatus.CONFIRMED,
                    OrderStatus.PREPARING,
                    OrderStatus.READY
                )
            }
            OrderFilterType.COMPLETED -> orders.filter { order ->
                order.status in listOf(
                    OrderStatus.DELIVERED,
                    OrderStatus.SERVED,
                    OrderStatus.COMPLETED
                )
            }
            OrderFilterType.CANCELLED -> orders.filter { order ->
                order.status == OrderStatus.CANCELLED
            }
        }
    }

    /**
     * Siparişi tekrar sipariş ver - FoodItem'ları Firebase'den çekip sepete ekler
     */
    fun reorder(order: Order, onSuccess: () -> Unit) {
        if (order.items.isEmpty() || order.tenantId.isBlank()) return
        
        uiState = uiState.copy(isReordering = true)
        
        viewModelScope.launch {
            try {
                // Sepeti temizle
                CartManager.clearCart()
                
                // Her OrderItem için FoodItem'ı çek ve sepete ekle
                for (orderItem in order.items) {
                    val menuItemId = orderItem.menuItemId ?: orderItem.id
                    
                    // Firebase'den MenuItem'ı çek
                    val result = tenantRepository.getMenuItem(order.tenantId, menuItemId)
                    
                    result.onSuccess { menuItem ->
                        if (menuItem != null) {
                            // MenuItem'ı FoodItem'a dönüştür
                            val foodItem = menuItem.toFoodItem()
                            
                            // OrderItem'daki customization'ları selectedOptions formatına çevir
                            val selectedSingleOptions = mutableMapOf<String, String>()
                            val selectedMultiOptions = mutableMapOf<String, Set<String>>()
                            
                            orderItem.customizations.forEach { customization ->
                                if (customization.selectedOptions.size == 1) {
                                    selectedSingleOptions[customization.groupId] = customization.selectedOptions.first().id
                                } else if (customization.selectedOptions.size > 1) {
                                    selectedMultiOptions[customization.groupId] = customization.selectedOptions.map { it.id }.toSet()
                                }
                            }
                            
                            // Removed ingredients'ı Set'e çevir
                            val removedItems = foodItem.removableItems
                                .filter { it.name in orderItem.removedIngredients }
                                .map { it.id }
                                .toSet()
                            
                            // Fiyat hesapla
                            val basePrice = foodItem.price.replace("₺", "").replace(",", ".").toDoubleOrNull() ?: 0.0
                            var totalPrice = basePrice * orderItem.quantity
                            
                            // Ekstra fiyatları ekle
                            orderItem.customizations.forEach { customization ->
                                customization.selectedOptions.forEach { option ->
                                    totalPrice += option.priceModifier * orderItem.quantity
                                }
                            }
                            
                            // Sepete ekle
                            CartManager.addToCart(
                                foodItem = foodItem,
                                quantity = orderItem.quantity,
                                selectedSingleOptions = selectedSingleOptions,
                                selectedMultiOptions = selectedMultiOptions,
                                removedItems = removedItems,
                                totalPrice = totalPrice
                            )
                        }
                    }.onFailure { e ->
                        println("OrdersViewModel: Failed to fetch menu item $menuItemId: ${e.message}")
                    }
                }
                
                uiState = uiState.copy(isReordering = false)
                
                // Başarılı callback - sepete yönlendirmek için
                if (!CartManager.isEmpty()) {
                    onSuccess()
                }
            } catch (e: Exception) {
                println("OrdersViewModel: Reorder failed: ${e.message}")
                uiState = uiState.copy(isReordering = false)
            }
        }
    }
}

data class OrdersUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isReordering: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String? = null,
    val selectedFilter: OrderFilterType = OrderFilterType.ALL,
    val selectedOrder: Order? = null // Detay ekranı için seçili sipariş
)
