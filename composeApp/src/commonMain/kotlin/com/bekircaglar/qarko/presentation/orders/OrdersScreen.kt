package com.bekircaglar.qarko.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bekircaglar.qarko.navigation.AppBottomBar
import com.bekircaglar.qarko.navigation.QRScan
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.*
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.ic_qr

// Dummy Order data
data class OrderItem(
    val id: String,
    val orderNumber: String,
    val date: String,
    val status: OrderStatus,
    val totalPrice: String,
    val items: List<String>,
    val restaurantName: String
)

enum class OrderStatus(val displayName: String, val color: Color) {
    DELIVERED("Teslim Edildi", Color(0xFF4CAF50)),
    PREPARING("Hazırlanıyor", Color(0xFFFF9800)),
    CANCELLED("İptal Edildi", Color(0xFFF44336))
}

val dummyOrders = listOf(
    OrderItem("1", "#2024001", "10 Ocak 2026, 14:30", OrderStatus.DELIVERED, "₺285", listOf("2x Margherita Pizza", "1x Cola"), "Qarko Cafe"),
    OrderItem("2", "#2024002", "9 Ocak 2026, 20:15", OrderStatus.DELIVERED, "₺180", listOf("1x Cheeseburger", "1x Patates", "1x Ayran"), "Qarko Cafe"),
    OrderItem("3", "#2024003", "8 Ocak 2026, 13:00", OrderStatus.CANCELLED, "₺95", listOf("1x Caesar Salad"), "Qarko Cafe"),
    OrderItem("4", "#2024004", "7 Ocak 2026, 19:45", OrderStatus.DELIVERED, "₺420", listOf("1x İskender", "2x Ayran", "1x Künefe"), "Qarko Cafe"),
    OrderItem("5", "#2024005", "11 Ocak 2026, 12:00", OrderStatus.PREPARING, "₺150", listOf("1x Latte", "1x Tiramisu", "1x Cheesecake"), "Qarko Cafe")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        QText(
                            text = "Siparişlerim",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkBlue
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = white
                    )
                )
            },
            bottomBar = {
                AppBottomBar(
                    navController = navController,
                    currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: ""
                )
            },
            containerColor = surfaceGray
        ) { paddingValues ->
            if (dummyOrders.isEmpty()) {
                // Boş durum
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        QText(
                            text = "📦",
                            fontSize = 64.sp
                        )
                        QText(
                            text = "Henüz siparişiniz yok",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkBlue
                        )
                        QText(
                            text = "İlk siparişinizi verin!",
                            fontSize = 14.sp,
                            color = gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(dummyOrders) { order ->
                        OrderItemCard(order)
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        // FAB Overlay
        Box(modifier = Modifier.align(Alignment.BottomCenter).zIndex(2f).padding(bottom = 24.dp)) {
            FloatingActionButton(
                onClick = { navController.navigate(QRScan) },
                containerColor = primary,
                contentColor = white,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_qr),
                    contentDescription = "QR",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun OrderItemCard(order: OrderItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Order number, date, status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    QText(
                        text = order.orderNumber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = darkBlue
                    )
                    QText(
                        text = order.date,
                        fontSize = 12.sp,
                        color = gray
                    )
                }

                // Status badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = order.status.color.copy(alpha = 0.15f)
                ) {
                    QText(
                        text = order.status.displayName,
                        color = order.status.color,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = lightGray.copy(alpha = 0.5f))

            // Restaurant name
            QText(
                text = order.restaurantName,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = darkBlue
            )

            // Items list
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                order.items.forEach { item ->
                    QText(
                        text = "• $item",
                        fontSize = 13.sp,
                        color = gray
                    )
                }
            }

            HorizontalDivider(color = lightGray.copy(alpha = 0.5f))

            // Footer: Total price and reorder button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    QText(text = "Toplam", fontSize = 12.sp, color = gray)
                    QText(
                        text = order.totalPrice,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = primary
                    )
                }

                if (order.status == OrderStatus.DELIVERED) {
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primary),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        QText(
                            text = "Tekrar Sipariş Ver",
                            color = white,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

