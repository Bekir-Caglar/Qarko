package com.bekircaglar.qarko.domain.usecase.order

import com.bekircaglar.qarko.data.manager.CartManager
import com.bekircaglar.qarko.data.manager.TenantSession
import com.bekircaglar.qarko.data.model.*
import com.bekircaglar.qarko.domain.repository.IOrderRepository
import kotlinx.datetime.Clock
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CreateOrderUseCase(
    private val orderRepository: IOrderRepository
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        paymentMethod: PaymentMethod,
        notes: String = ""
    ): Result<String> {
        val tenantId = TenantSession.tenantId ?: return Result.failure(Exception("Tenant not found"))
        val table = TenantSession.currentTable
        val cartItems = CartManager.cartItems

        if (cartItems.isEmpty()) {
            return Result.failure(Exception("Cart is empty"))
        }

        val orderItems = cartItems.map { cartItem ->
            OrderItem(
                id = Uuid.random().toString(),
                menuItemId = cartItem.foodId,
                name = cartItem.name,
                quantity = cartItem.quantity,
                price = cartItem.price,
                originalPrice = cartItem.basePrice,
                totalPrice = cartItem.price * cartItem.quantity,
                removedIngredients = cartItem.removedItems.toList(),
                notes = cartItem.note,
                customizations = mapCustomizations(cartItem)
            )
        }

        val pricing = OrderPricing(
            subtotal = CartManager.totalPrice,
            total = CartManager.totalPrice,
            discount = 0.0
        )

        val order = Order(
            orderNumber = generateOrderNumber(), // 8 haneli rastgele sayı
            tenantId = tenantId,
            tableId = table?.id,
            tableNumber = table?.name?.filter { it.isDigit() }?.toIntOrNull(),
            type = OrderType.DINE_IN,
            status = OrderStatus.PENDING,
            items = orderItems,
            pricing = pricing,
            paymentMethod = paymentMethod.name,
            paymentStatus = if (paymentMethod == PaymentMethod.ONLINE) PaymentStatus.COMPLETED.name else PaymentStatus.PENDING.name,
            notes = notes,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )

        return orderRepository.createOrder(order)
    }

    private fun generateOrderNumber(): String {
        return (1..8).map { Random.nextInt(0, 10) }.joinToString("")
    }

    private fun mapCustomizations(cartItem: CartItemData): List<OrderItemCustomization> {
        val customizations = mutableListOf<OrderItemCustomization>()

        cartItem.selectedOptions.forEach { (groupId, optionId) ->
            customizations.add(
                OrderItemCustomization(
                    groupId = groupId,
                    groupName = "",
                    selectedOptions = listOf(
                        OrderItemCustomizationOption(
                            id = optionId,
                            name = "",
                            priceModifier = 0.0
                        )
                    )
                )
            )
        }

        cartItem.selectedMultiOptions.forEach { (groupId, optionIds) ->
            customizations.add(
                OrderItemCustomization(
                    groupId = groupId,
                    groupName = "",
                    selectedOptions = optionIds.map { optionId ->
                        OrderItemCustomizationOption(
                            id = optionId,
                            name = "",
                            priceModifier = 0.0
                        )
                    }
                )
            )
        }

        return customizations
    }
}
