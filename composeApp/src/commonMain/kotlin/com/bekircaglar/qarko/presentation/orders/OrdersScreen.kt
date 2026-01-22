package com.bekircaglar.qarko.presentation.orders

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bekircaglar.qarko.data.manager.UserManager
import com.bekircaglar.qarko.data.model.Order
import com.bekircaglar.qarko.data.model.OrderItem
import com.bekircaglar.qarko.data.model.OrderStatus
import com.bekircaglar.qarko.data.model.OrderType
import com.bekircaglar.qarko.navigation.AppBottomBar
import com.bekircaglar.qarko.navigation.Auth
import com.bekircaglar.qarko.navigation.Cart
import com.bekircaglar.qarko.navigation.QRScan
import com.bekircaglar.qarko.presentation.common.components.LoginRequiredContent
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.*
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.ic_qr

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun OrdersScreen(
    navController: NavController,
    viewModel: OrdersViewModel = koinViewModel()
) {
    val isLoggedIn = UserManager.isLoggedIn
    val uiState = viewModel.uiState

    // Pull Refresh State
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { viewModel.refresh() }
    )

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
            if (!isLoggedIn) {
                LoginRequiredContent(
                    title = "Siparişlerinizi Görüntüleyin",
                    description = "Geçmiş siparişlerinizi takip etmek ve tekrar sipariş vermek için giriş yapın.",
                    featureList = listOf(
                        "Sipariş geçmişinizi görüntüleyin",
                        "Siparişlerinizi anlık takip edin",
                        "Tek tıkla tekrar sipariş verin",
                        "E-faturalarınıza erişin"
                    ),
                    onLoginClick = { navController.navigate(Auth) },
                    modifier = Modifier.padding(paddingValues)
                )
            } else if (uiState.isLoading) {
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
                        CircularProgressIndicator(color = primary)
                        QText(text = "Siparişler yükleniyor...", fontSize = 14.sp, color = gray)
                    }
                }
            } else if (uiState.error != null) {
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
                        Icon(
                            imageVector = FeatherIcons.AlertCircle,
                            contentDescription = null,
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(64.dp)
                        )
                        QText(
                            text = uiState.error,
                            fontSize = 16.sp,
                            color = gray,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.refresh() },
                            colors = ButtonDefaults.buttonColors(containerColor = primary)
                        ) {
                            QText(text = "Tekrar Dene", color = white)
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Filter Tab Bar
                    OrderFilterTabs(
                        selectedFilter = uiState.selectedFilter,
                        onFilterSelected = { viewModel.setFilter(it) }
                    )

                    // Orders List with Pull to Refresh
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pullRefresh(pullRefreshState)
                    ) {
                        if (uiState.orders.isEmpty()) {
                            EmptyOrdersView(filter = uiState.selectedFilter)
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(uiState.orders, key = { it.id }) { order ->
                                    OrderItemCard(
                                        order = order,
                                        onClick = { viewModel.selectOrder(order) },
                                        onReorder = {
                                            viewModel.reorder(order) {
                                                navController.navigate(Cart)
                                            }
                                        }
                                    )
                                }
                                item { Spacer(modifier = Modifier.height(80.dp)) }
                            }
                        }

                        // Pull Refresh Indicator
                        PullRefreshIndicator(
                            refreshing = uiState.isRefreshing,
                            state = pullRefreshState,
                            modifier = Modifier.align(Alignment.TopCenter),
                            contentColor = primary
                        )
                    }
                }
            }
        }

        // Floating QR Button
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

    // Order Detail Dialog
    uiState.selectedOrder?.let { order ->
        OrderDetailDialog(
            order = order,
            onDismiss = { viewModel.selectOrder(null) }
        )
    }
}

// ==================== Filter Tabs ====================

@Composable
private fun OrderFilterTabs(
    selectedFilter: OrderFilterType,
    onFilterSelected: (OrderFilterType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OrderFilterType.entries.forEach { filter ->
            FilterChipItem(
                filter = filter,
                isSelected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}

@Composable
private fun FilterChipItem(
    filter: OrderFilterType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(200),
        label = "chipScale"
    )

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .scale(scale)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) primary else lightGray.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = getFilterIcon(filter),
                contentDescription = null,
                tint = if (isSelected) white else gray,
                modifier = Modifier.size(16.dp)
            )
            QText(
                text = filter.displayName,
                color = if (isSelected) white else darkBlue,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}

private fun getFilterIcon(filter: OrderFilterType): ImageVector {
    return when (filter) {
        OrderFilterType.ALL -> FeatherIcons.List
        OrderFilterType.ACTIVE -> FeatherIcons.Clock
        OrderFilterType.COMPLETED -> FeatherIcons.CheckCircle
        OrderFilterType.CANCELLED -> FeatherIcons.XCircle
    }
}

// ==================== Empty State ====================

@Composable
private fun EmptyOrdersView(filter: OrderFilterType) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            val (icon, iconColor, title, subtitle) = when (filter) {
                OrderFilterType.ALL -> listOf(FeatherIcons.Package, gray, "Henüz siparişiniz yok", "İlk siparişinizi verin!")
                OrderFilterType.ACTIVE -> listOf(FeatherIcons.Clock, Color(0xFFFF9800), "Aktif sipariş yok", "Şu anda işlenen siparişiniz bulunmuyor")
                OrderFilterType.COMPLETED -> listOf(FeatherIcons.CheckCircle, Color(0xFF4CAF50), "Tamamlanan sipariş yok", "Henüz tamamlanan siparişiniz bulunmuyor")
                OrderFilterType.CANCELLED -> listOf(FeatherIcons.XCircle, Color(0xFFF44336), "İptal edilen sipariş yok", "İptal edilmiş siparişiniz bulunmuyor")
            }

            Icon(
                imageVector = icon as ImageVector,
                contentDescription = null,
                tint = iconColor as Color,
                modifier = Modifier.size(64.dp)
            )
            QText(
                text = title as String,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = darkBlue
            )
            QText(
                text = subtitle as String,
                fontSize = 14.sp,
                color = gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==================== Order Card ====================

@Composable
private fun OrderItemCard(
    order: Order,
    onClick: () -> Unit,
    onReorder: () -> Unit
) {
    val statusInfo = getStatusInfo(order.status)
    val formattedDate = formatDate(order.createdAt)
    val formattedPrice = "₺${order.pricing?.total?.toInt() ?: order.totalAmount?.toInt() ?: 0}"
    val restaurantName = order.tenantName ?: "Restoran"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
            // Header: Order Number + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    QText(
                        text = "#${order.orderNumber ?: order.id.take(8)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = darkBlue
                    )
                    QText(text = formattedDate, fontSize = 12.sp, color = gray)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusInfo.color.copy(alpha = 0.15f)
                ) {
                    QText(
                        text = statusInfo.displayName,
                        color = statusInfo.color,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = lightGray.copy(alpha = 0.5f))

            // Restaurant Name + Order Type Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QText(
                    text = restaurantName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = darkBlue,
                    modifier = Modifier.weight(1f)
                )

                // Order Type Badge
                OrderTypeBadge(orderType = order.type, tableNumber = order.tableNumber)
            }

            // Items Preview
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                order.items.take(3).forEach { item ->
                    QText(text = "• ${item.quantity}x ${item.name}", fontSize = 13.sp, color = gray)
                }
                if (order.items.size > 3) {
                    QText(text = "... ve ${order.items.size - 3} ürün daha", fontSize = 12.sp, color = gray)
                }
            }

            // Notes (if any)
            if (!order.notes.isNullOrBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = FeatherIcons.FileText,
                        contentDescription = null,
                        tint = gray,
                        modifier = Modifier.size(12.dp)
                    )
                    QText(text = order.notes, fontSize = 12.sp, color = gray)
                }
            }

            HorizontalDivider(color = lightGray.copy(alpha = 0.5f))

            // Footer: Total + Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    QText(text = "Toplam", fontSize = 12.sp, color = gray)
                    QText(
                        text = formattedPrice,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = primary
                    )
                }

                StatusActionSection(status = order.status, onReorder = onReorder)
            }
        }
    }
}

@Composable
private fun OrderTypeBadge(orderType: OrderType?, tableNumber: Int?) {
    val (icon, text, bgColor) = when (orderType) {
        OrderType.DINE_IN -> Triple(
            FeatherIcons.Home,
            "Masa ${tableNumber ?: "-"}",
            Color(0xFF2196F3).copy(alpha = 0.15f)
        )
        OrderType.TAKEAWAY -> Triple(
            FeatherIcons.ShoppingBag,
            "Paket",
            Color(0xFF4CAF50).copy(alpha = 0.15f)
        )
        OrderType.DELIVERY -> Triple(
            FeatherIcons.Truck,
            "Teslimat",
            Color(0xFFFF9800).copy(alpha = 0.15f)
        )
        null -> Triple(
            FeatherIcons.ShoppingBag,
            "Sipariş",
            lightGray.copy(alpha = 0.5f)
        )
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = gray
            )
            QText(text = text, fontSize = 11.sp, color = gray, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun StatusActionSection(status: OrderStatus, onReorder: () -> Unit) {
    when (status) {
        OrderStatus.PENDING -> {
            StatusBadgeWithIcon(text = "Onay Bekleniyor", icon = FeatherIcons.Clock, color = Color(0xFFFF9800), bgColor = Color(0xFFFFF3E0))
        }
        OrderStatus.CONFIRMED -> {
            StatusBadgeWithIcon(text = "Onaylandı", icon = FeatherIcons.Check, color = Color(0xFF2196F3), bgColor = Color(0xFFE3F2FD))
        }
        OrderStatus.PREPARING -> {
            StatusBadgeWithIcon(text = "Hazırlanıyor", icon = FeatherIcons.Coffee, color = Color(0xFF2196F3), bgColor = Color(0xFFE3F2FD))
        }
        OrderStatus.READY -> {
            StatusBadgeWithIcon(text = "Hazır!", icon = FeatherIcons.Bell, color = Color(0xFF4CAF50), bgColor = Color(0xFFE8F5E9))
        }
        OrderStatus.SERVED -> {
            StatusBadgeWithIcon(text = "Servis Edildi", icon = FeatherIcons.CheckCircle, color = Color(0xFF4CAF50), bgColor = Color(0xFFE8F5E9))
        }
        OrderStatus.DELIVERED, OrderStatus.COMPLETED -> {
            Button(
                onClick = onReorder,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primary),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                QText(text = "Tekrar Sipariş Ver", color = white, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }
        OrderStatus.CANCELLED -> {
            StatusBadgeWithIcon(text = "İptal Edildi", icon = FeatherIcons.X, color = Color(0xFFF44336), bgColor = Color(0xFFFFEBEE))
        }
    }
}

@Composable
private fun StatusBadgeWithIcon(text: String, icon: ImageVector, color: Color, bgColor: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            QText(
                text = text,
                color = color,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
        }
    }
}

// ==================== Order Detail Dialog ====================

@Composable
private fun OrderDetailDialog(
    order: Order,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = white)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Dialog Header
                DialogHeader(order = order, onDismiss = onDismiss)

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Status Section
                    OrderStatusSection(order)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Restaurant & Table Info
                    OrderLocationSection(order)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Items List
                    OrderItemsSection(order)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Pricing Summary
                    OrderPricingSection(order)

                    // Notes Section
                    if (!order.notes.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        OrderNotesSection(order.notes)
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun DialogHeader(order: Order, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(surfaceGray)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            QText(
                text = "Sipariş Detayı",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = darkBlue
            )
            QText(
                text = "#${order.orderNumber ?: order.id.take(8)}",
                fontSize = 14.sp,
                color = gray
            )
        }

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .size(36.dp)
                .background(lightGray.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = FeatherIcons.X,
                contentDescription = "Kapat",
                tint = darkBlue,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun OrderStatusSection(order: Order) {
    val statusInfo = getStatusInfo(order.status)
    val formattedDate = formatDate(order.createdAt)

    DetailCard(title = "Durum") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusInfo.color.copy(alpha = 0.15f)
                ) {
                    QText(
                        text = statusInfo.displayName,
                        color = statusInfo.color,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = FeatherIcons.Calendar,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = gray
                    )
                    QText(text = formattedDate, fontSize = 13.sp, color = gray)
                }
            }

            // Order Type Icon
            OrderTypeBadge(orderType = order.type, tableNumber = order.tableNumber)
        }
    }
}

@Composable
private fun OrderLocationSection(order: Order) {
    DetailCard(title = "Konum Bilgisi") {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = FeatherIcons.MapPin,
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                QText(
                    text = order.tenantName ?: "Restoran",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = darkBlue
                )
                if (order.tableNumber != null || order.table?.name != null) {
                    QText(
                        text = order.table?.name ?: "Masa ${order.tableNumber}",
                        fontSize = 14.sp,
                        color = gray
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderItemsSection(order: Order) {
    DetailCard(title = "Ürünler (${order.items.size})") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            order.items.forEach { item ->
                OrderItemRow(item = item)
            }
        }
    }
}

@Composable
private fun OrderItemRow(item: OrderItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(surfaceGray, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Quantity Badge
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(primary, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    QText(
                        text = "${item.quantity}x",
                        color = white,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    QText(
                        text = item.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = darkBlue
                    )

                    // Customizations
                    item.customizations.forEach { customization ->
                        customization.selectedOptions.forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = FeatherIcons.Plus,
                                    contentDescription = null,
                                    modifier = Modifier.size(10.dp),
                                    tint = Color(0xFF4CAF50)
                                )
                                QText(
                                    text = option.name,
                                    fontSize = 12.sp,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }

                    // Removed Ingredients
                    item.removedIngredients.forEach { removed ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = FeatherIcons.Minus,
                                contentDescription = null,
                                modifier = Modifier.size(10.dp),
                                tint = Color(0xFFF44336)
                            )
                            QText(
                                text = removed,
                                fontSize = 12.sp,
                                color = Color(0xFFF44336),
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }
                }
            }

            // Price
            QText(
                text = "₺${item.totalPrice?.toInt() ?: (item.price * item.quantity).toInt()}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = darkBlue
            )
        }

        // Item Notes
        if (!item.notes.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF8E1), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = FeatherIcons.MessageSquare,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFFFFA000)
                )
                QText(
                    text = item.notes,
                    fontSize = 12.sp,
                    color = Color(0xFFF57C00)
                )
            }
        }
    }
}

@Composable
private fun OrderPricingSection(order: Order) {
    val pricing = order.pricing

    DetailCard(title = "Fiyat Özeti") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Subtotal
            PricingRow(label = "Ara Toplam", value = "₺${pricing?.subtotal?.toInt() ?: 0}")

            // Discount (if any)
            if ((pricing?.discount ?: 0.0) > 0) {
                PricingRow(
                    label = "İndirim",
                    value = "-₺${pricing?.discount?.toInt()}",
                    valueColor = Color(0xFF4CAF50)
                )
            }

            // Service Fee (if any)
            if ((pricing?.serviceFee ?: 0.0) > 0) {
                PricingRow(label = "Hizmet Bedeli", value = "₺${pricing?.serviceFee?.toInt()}")
            }

            // Tip (if any)
            if ((pricing?.tip ?: 0.0) > 0) {
                PricingRow(label = "Bahşiş", value = "₺${pricing?.tip?.toInt()}")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = lightGray)

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QText(
                    text = "Toplam",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkBlue
                )
                QText(
                    text = "₺${pricing?.total?.toInt() ?: order.totalAmount?.toInt() ?: 0}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primary
                )
            }

            // Payment Info
            if (order.paymentMethod != null || order.paymentStatus != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(surfaceGray, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (order.paymentMethod != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = FeatherIcons.CreditCard,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = gray
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            QText(text = getPaymentMethodText(order.paymentMethod), fontSize = 13.sp, color = gray)
                        }
                    }
                    if (order.paymentStatus != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (order.paymentStatus.uppercase() in listOf("COMPLETED", "PAID")) FeatherIcons.CheckCircle else FeatherIcons.Clock,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (order.paymentStatus.uppercase() in listOf("COMPLETED", "PAID")) Color(0xFF4CAF50) else gray
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            QText(text = getPaymentStatusText(order.paymentStatus), fontSize = 13.sp, color = gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PricingRow(label: String, value: String, valueColor: Color = darkBlue) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QText(text = label, fontSize = 14.sp, color = gray)
        QText(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = valueColor)
    }
}

@Composable
private fun OrderNotesSection(notes: String) {
    DetailCard(title = "Sipariş Notu") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF8E1), RoundedCornerShape(8.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = FeatherIcons.MessageCircle,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color(0xFFFFA000)
            )
            QText(
                text = notes,
                fontSize = 14.sp,
                color = Color(0xFFF57C00)
            )
        }
    }
}

@Composable
private fun DetailCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        QText(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = white),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

// ==================== Helper Functions ====================

private data class StatusInfo(val displayName: String, val color: Color)

private fun getStatusInfo(status: OrderStatus): StatusInfo {
    return when (status) {
        OrderStatus.PENDING -> StatusInfo("Onay Bekleniyor", Color(0xFFFF9800))
        OrderStatus.CONFIRMED -> StatusInfo("Onaylandı", Color(0xFF2196F3))
        OrderStatus.PREPARING -> StatusInfo("Hazırlanıyor", Color(0xFF2196F3))
        OrderStatus.READY -> StatusInfo("Hazır", Color(0xFF4CAF50))
        OrderStatus.SERVED -> StatusInfo("Servis Edildi", Color(0xFF4CAF50))
        OrderStatus.DELIVERED -> StatusInfo("Teslim Edildi", Color(0xFF4CAF50))
        OrderStatus.COMPLETED -> StatusInfo("Tamamlandı", Color(0xFF4CAF50))
        OrderStatus.CANCELLED -> StatusInfo("İptal Edildi", Color(0xFFF44336))
    }
}

private fun formatDate(instant: Instant?): String {
    if (instant == null) return ""
    return try {
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val month = when (localDateTime.monthNumber) {
            1 -> "Ocak"; 2 -> "Şubat"; 3 -> "Mart"; 4 -> "Nisan"; 5 -> "Mayıs"; 6 -> "Haziran"
            7 -> "Temmuz"; 8 -> "Ağustos"; 9 -> "Eylül"; 10 -> "Ekim"; 11 -> "Kasım"; 12 -> "Aralık"
            else -> ""
        }
        "${localDateTime.dayOfMonth} $month ${localDateTime.year}, ${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
    } catch (e: Exception) { "" }
}

private fun getPaymentMethodText(method: String): String {
    return when (method.uppercase()) {
        "CREDIT_CARD" -> "Kredi Kartı"
        "CASH" -> "Nakit"
        "ONLINE" -> "Online"
        else -> method
    }
}

private fun getPaymentStatusText(status: String): String {
    return when (status.uppercase()) {
        "PENDING" -> "Ödeme Bekleniyor"
        "COMPLETED", "PAID" -> "Ödendi"
        "FAILED" -> "Ödeme Başarısız"
        "REFUNDED" -> "İade Edildi"
        else -> status
    }
}
