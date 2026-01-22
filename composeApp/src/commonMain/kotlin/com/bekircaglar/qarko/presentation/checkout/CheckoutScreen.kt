package com.bekircaglar.qarko.presentation.checkout

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.bekircaglar.qarko.data.manager.CartManager
import com.bekircaglar.qarko.presentation.common.components.BackButton
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.black
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.presentation.common.theme.lighterGray
import com.bekircaglar.qarko.presentation.common.theme.primary
import com.bekircaglar.qarko.presentation.common.theme.surfaceGray
import com.bekircaglar.qarko.presentation.common.theme.white
import compose.icons.FeatherIcons
import compose.icons.feathericons.AlertCircle
import compose.icons.feathericons.Check
import compose.icons.feathericons.ChevronDown
import compose.icons.feathericons.ChevronUp
import compose.icons.feathericons.CreditCard
import compose.icons.feathericons.Printer
import compose.icons.feathericons.Plus
import org.jetbrains.compose.resources.painterResource
import qarko.composeapp.generated.resources.Res
import qarko.composeapp.generated.resources.mastercard
import androidx.compose.foundation.Image
import com.bekircaglar.qarko.data.manager.TenantSession
import com.bekircaglar.qarko.data.manager.UserManager
import com.bekircaglar.qarko.data.model.CardBrand
import com.bekircaglar.qarko.data.model.SavedCard
import com.bekircaglar.qarko.navigation.Orders
import com.bekircaglar.qarko.navigation.QRVerification
import com.bekircaglar.qarko.navigation.TenantMenu
import com.bekircaglar.qarko.presentation.checkout.component.AddCardBottomSheet
import com.bekircaglar.qarko.presentation.checkout.component.OrderSuccessDialog
import com.bekircaglar.qarko.presentation.checkout.component.QRVerificationBottomSheet
import org.koin.compose.viewmodel.koinViewModel

private data class PaymentTab(
    val title: String,
    val icon: ImageVector
)

private val paymentTabs = listOf(
    PaymentTab("Kart ile Öde", FeatherIcons.CreditCard),
    PaymentTab("Kasada Öde", FeatherIcons.Printer)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CheckoutViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()
    
    // Gerçek sepet verileri
    val liveCartTotal = CartManager.totalPrice
    val liveOrderNote = uiState.orderNote
    
    // Success dialog açıkken gösterilecek geçici veriler (cart temizlenmeden önceki değerler)
    var savedTotalForDialog by remember { mutableStateOf<Double?>(null) }
    var savedNoteForDialog by remember { mutableStateOf<String?>(null) }
    var savedDiscountForDialog by remember { mutableStateOf<Double?>(null) }
    
    // Eğer dialog açıksa kayıtlı verileri kullan, değilse canlı verileri
    val cartTotal = savedTotalForDialog ?: liveCartTotal
    val discountAmount = savedDiscountForDialog ?: uiState.discountAmount
    val totalAmount = cartTotal - discountAmount
    val isZeroTotal = totalAmount <= 0

    var selectedCardIndex by remember { mutableStateOf(0) }
    var isCardListExpanded by remember { mutableStateOf(false) }
    var showAddCardSheet by remember { mutableStateOf(false) }
    var showQRVerification by remember { mutableStateOf(false) }
    var pendingQRVerification by rememberSaveable { mutableStateOf(false) } // rememberSaveable ile navigation sırasında korunur
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    // Snackbar için
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Gerçek kayıtlı kartları kullan
    val savedCards = UserManager.savedCards
    
    // Masa bilgisi
    val tableNumber = TenantSession.currentTable?.name
    val expectedTableId = remember { TenantSession.tableId }

    // Kayıtlı kartları yükle
    LaunchedEffect(Unit) {
        viewModel.loadSavedCards()
    }

    // Kart listesi değiştiğinde seçili index'i kontrol et
    LaunchedEffect(savedCards.size) {
        if (selectedCardIndex >= savedCards.size && savedCards.isNotEmpty()) {
            selectedCardIndex = 0
        }
    }
    
    // QR Verification ekranından dönüldüğünde sonucu al (StateFlow ile reactive)
    val qrVerificationResult by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<Boolean?>("qr_verification_result", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }
    
    LaunchedEffect(qrVerificationResult) {
        println("CheckoutScreen: qrVerificationResult = $qrVerificationResult, pendingQRVerification = $pendingQRVerification")
        
        if (pendingQRVerification && qrVerificationResult != null) {
            println("CheckoutScreen: Processing QR verification result...")
            pendingQRVerification = false
            // Sonucu temizle (tekrar tetiklememesi için)
            navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("qr_verification_result")
            
            if (qrVerificationResult == true) {
                // Doğrulama başarılı, sipariş ver
                println("CheckoutScreen: Verification SUCCESS! Placing order...")
                viewModel.verifyQRCode(expectedTableId ?: "")
                // Verileri kaydet ve sipariş ver
                savedTotalForDialog = liveCartTotal
                savedDiscountForDialog = uiState.discountAmount
                savedNoteForDialog = liveOrderNote
                viewModel.placeOrder()
            } else {
                // Doğrulama başarısız
                println("CheckoutScreen: Verification FAILED/CANCELLED")
                scope.launch {
                    snackbarHostState.showSnackbar("QR doğrulama iptal edildi")
                }
            }
        }
    }
    
    // Error mesajlarını göster
    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMsg ->
            snackbarHostState.showSnackbar(errorMsg)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CheckoutEvent.OrderSuccess -> {
                    // Hemen yönlendirmek yerine success dialog göster
                    showSuccessDialog = true
                }
            }
        }
    }
    
    // Sipariş vermeden önce verileri kaydet (lambda olarak tanımlanmalı, Composable içinde fun kullanılamaz)
    val saveDataAndPlaceOrder: () -> Unit = {
        // Mevcut değerleri kaydet (cart temizlenmeden önce)
        savedTotalForDialog = liveCartTotal
        savedDiscountForDialog = uiState.discountAmount
        savedNoteForDialog = liveOrderNote
        // Sipariş ver
        viewModel.placeOrder()
    }

    Scaffold(
        containerColor = surfaceGray,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = white,
                ),
                title = {
                    QText(
                        text = "Ödeme",
                        fontSize = 18.sp,
                        color = black,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    BackButton { navController.popBackStack() }
                }
            )
        },
        bottomBar = {
            CheckoutBottomBar(
                totalAmount = totalAmount,
                isLoading = uiState.isLoading
            ) {
                // QR doğrulama gerekli mi kontrol et
                if (viewModel.requiresQRVerification()) {
                    showQRVerification = true
                } else {
                    saveDataAndPlaceOrder()
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Order Note Section
            SectionCard(title = "Sipariş Notu") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(lighterGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .border(1.dp, gray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    if (uiState.orderNote.isEmpty()) {
                        Text(
                            text = "Siparişiniz ile ilgili eklemek istediğiniz bir not var mı?",
                            color = gray,
                            fontSize = 14.sp
                        )
                    }
                    BasicTextField(
                        value = uiState.orderNote,
                        onValueChange = { viewModel.onOrderNoteChange(it) },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 14.sp,
                            color = black
                        ),
                        modifier = Modifier.fillMaxSize()
                    )

                    Text(
                        text = "${uiState.orderNote.length}/200",
                        fontSize = 10.sp,
                        color = gray,
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                }
            }

            // Campaigns Section
            SectionCard(title = "Kampanyalar") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(lighterGray.copy(alpha = 0.2f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(primary.copy(alpha = 0.15f), RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎁", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.size(12.dp))
                        Column {
                            Text(
                                text = uiState.selectedCampaign?.title ?: "Kampanya Seç",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = black
                            )
                            Text(
                                text = if (uiState.selectedCampaign != null) "Kampanya uygulandı" else "Mevcut kampanyalarınızı kullanın",
                                fontSize = 12.sp,
                                color = if (uiState.selectedCampaign != null) primary else gray,
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = { navController.navigate(com.bekircaglar.qarko.navigation.CampaignSelect) },
                        border = BorderStroke(1.dp, primary),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = white
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (uiState.selectedCampaign != null) "Değiştir" else "Seç",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = primary
                        )
                    }
                }
            }

            // Payment Method Section
            SectionCard(title = "Ödeme Yöntemi") {
                Column {
                    TabRow(
                        selectedTabIndex = uiState.selectedPaymentMethodIndex,
                        containerColor = white,
                        contentColor = primary,
                        indicator = { tabPositions ->
                            Box(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[uiState.selectedPaymentMethodIndex])
                                    .height(3.dp)
                                    .padding(horizontal = 32.dp)
                                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                    .background(primary)
                            )
                        }
                    ) {
                        paymentTabs.forEachIndexed { index, tab ->
                            val isSelected = uiState.selectedPaymentMethodIndex == index
                            val textColor by animateColorAsState(
                                targetValue = if (isSelected) primary else gray,
                                label = "tabTextColor"
                            )
                            
                            // Sepet 0 TL ise Online Ödeme tab'ını devre dışı bırak
                            val isEnabled = !(isZeroTotal && index == 0)

                            Tab(
                                selected = isSelected,
                                onClick = { if (isEnabled) viewModel.onPaymentMethodChange(index) },
                                enabled = isEnabled,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = null,
                                        tint = if (isEnabled) textColor else gray.copy(alpha = 0.5f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.size(8.dp))
                                    QText(
                                        text = tab.title,
                                        color = if (isEnabled) textColor else gray.copy(alpha = 0.5f),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (uiState.selectedPaymentMethodIndex) {
                        0 -> {
                            if (savedCards.isEmpty()) {
                                // Kayıtlı kart yoksa direkt "Kart Ekle" göster
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .drawBehind {
                                            val stroke = Stroke(
                                                width = 3f,
                                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                                            )
                                            drawRoundRect(
                                                color = Color.Gray,
                                                style = stroke,
                                                cornerRadius = CornerRadius(8.dp.toPx())
                                            )
                                        }
                                        .clickable { showAddCardSheet = true }
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = FeatherIcons.Plus,
                                            contentDescription = null,
                                            tint = primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.size(12.dp))
                                        Text(
                                            text = "Yeni Kart Ekle",
                                            color = primary,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            } else {
                                // Kayıtlı kartlar var
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(lighterGray.copy(alpha = 0.2f))
                                ) {
                                    val selectedCard = savedCards.getOrNull(selectedCardIndex) ?: savedCards.first()
                                    
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { isCardListExpanded = !isCardListExpanded }
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Image(
                                                painter = painterResource(Res.drawable.mastercard),
                                                contentDescription = selectedCard.cardBrand.name,
                                                modifier = Modifier.size(32.dp)
                                            )
                                            Spacer(modifier = Modifier.size(12.dp))
                                            Column {
                                                Text(
                                                    text = selectedCard.cardName,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = black
                                                )
                                                Text(
                                                    text = "**** **** **** ${selectedCard.lastFourDigits}",
                                                    fontSize = 12.sp,
                                                    color = gray
                                                )
                                            }
                                        }

                                        Icon(
                                            imageVector = if (isCardListExpanded) FeatherIcons.ChevronUp else FeatherIcons.ChevronDown,
                                            contentDescription = null,
                                            tint = gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    if (isCardListExpanded) {
                                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                            HorizontalDivider(color = gray.copy(alpha = 0.1f))
                                            savedCards.forEachIndexed { index, card ->
                                                if (index != selectedCardIndex) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable {
                                                                selectedCardIndex = index
                                                                isCardListExpanded = false
                                                            }
                                                            .padding(vertical = 12.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Image(
                                                            painter = painterResource(Res.drawable.mastercard),
                                                            contentDescription = card.cardBrand.name,
                                                            modifier = Modifier.size(32.dp)
                                                        )
                                                        Spacer(modifier = Modifier.size(12.dp))
                                                        Column {
                                                            Text(
                                                                text = card.cardName,
                                                                fontSize = 14.sp,
                                                                fontWeight = FontWeight.Medium,
                                                                color = black
                                                            )
                                                            Text(
                                                                text = "**** **** **** ${card.lastFourDigits}",
                                                                fontSize = 12.sp,
                                                                color = gray
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 8.dp)
                                                    .drawBehind {
                                                        val stroke = Stroke(
                                                            width = 3f,
                                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                                                        )
                                                        drawRoundRect(
                                                            color = Color.Gray,
                                                            style = stroke,
                                                            cornerRadius = CornerRadius(8.dp.toPx())
                                                        )
                                                    }
                                                    .clickable { showAddCardSheet = true }
                                                    .padding(12.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = FeatherIcons.Plus,
                                                        contentDescription = null,
                                                        tint = gray,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.size(8.dp))
                                                    Text(
                                                        text = "Kart Ekle",
                                                        color = gray,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        1 -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(primary.copy(alpha = 0.1f))
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = FeatherIcons.AlertCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = primary
                                )
                                Text(
                                    text = if (isZeroTotal) "Sipariş tutarınız 0 TL olduğu için ödemenizi doğrudan onaylayabilirsiniz." else "Siparişiniz hazırlandığında kasada nakit veya kredi kartı ile ödeme yapabilirsiniz.",
                                    fontSize = 14.sp,
                                    color = black,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            // Payment Summary Section
            SectionCard(title = "Ödeme Özeti") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Ürün Listesi
                    CartManager.cartItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${item.quantity}x ${item.name}",
                                    fontSize = 14.sp,
                                    color = black,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "₺${(item.price * item.quantity).toInt()}",
                                fontSize = 14.sp,
                                color = black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = gray.copy(alpha = 0.1f)
                    )

                    SummaryRow(title = "Ara Toplam", amount = cartTotal)
                    
                    if (uiState.discountAmount > 0) {
                        SummaryRow(
                            title = uiState.selectedCampaign?.title ?: "İndirim",
                            amount = uiState.discountAmount,
                            isDiscount = true
                        )
                    }
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = gray.copy(alpha = 0.2f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Toplam",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = black
                        )
                        Text(
                            text = "₺${totalAmount.toInt()}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Kart Ekleme Bottom Sheet
    if (showAddCardSheet) {
        AddCardBottomSheet(
            onDismiss = { showAddCardSheet = false },
            onSaveCard = { card ->
                viewModel.saveCard(card) {
                    showAddCardSheet = false
                    // Yeni eklenen kartı seçili yap
                    selectedCardIndex = savedCards.size // Yeni kart en sona eklenecek
                }
            },
            isLoading = uiState.isSavingCard
        )
    }

    // QR Doğrulama Bottom Sheet
    if (showQRVerification) {
        QRVerificationBottomSheet(
            onDismiss = { showQRVerification = false },
            onScanQR = {
                showQRVerification = false
                pendingQRVerification = true // QR tarama sonrası doğrulama bekle
                // Yeni QR Verification ekranına git
                navController.navigate(QRVerification(expectedTableId = expectedTableId ?: ""))
            },
            tableNumber = tableNumber
        )
    }

    // Sipariş Başarılı Dialog
    if (showSuccessDialog) {
        OrderSuccessDialog(
            onDismiss = {
                showSuccessDialog = false
                // Geçici verileri temizle
                savedTotalForDialog = null
                savedDiscountForDialog = null
                savedNoteForDialog = null
                // Menüye dön
                navController.navigate(TenantMenu) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = false
                    }
                }
            },
            onGoToOrders = {
                showSuccessDialog = false
                // Geçici verileri temizle
                savedTotalForDialog = null
                savedDiscountForDialog = null
                savedNoteForDialog = null
                // Siparişlerim ekranına git
                navController.navigate(Orders) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = false
                    }
                }
            }
        )
    }
}

@Composable
fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(12.dp))
            .background(white, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
fun SummaryRow(title: String, amount: Double, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = if (isDiscount) primary else gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "${if (isDiscount) "-" else ""}₺${amount.toInt()}",
            fontSize = 14.sp,
            color = if (isDiscount) primary else black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CheckoutBottomBar(
    totalAmount: Double,
    isLoading: Boolean,
    onPlaceOrder: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 16.dp,
        color = white
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = onPlaceOrder,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = white, modifier = Modifier.size(24.dp))
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = FeatherIcons.Check,
                            contentDescription = null,
                            tint = white,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Siparişi Onayla - ₺ ${totalAmount.toInt()}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = white
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val annotatedString = buildAnnotatedString {
                append("Sipariş vererek ")
                withStyle(style = SpanStyle(color = primary, fontWeight = FontWeight.Bold)) {
                    append("Kullanım Koşulları")
                }
                append("'nı kabul ediyorsunuz")
            }

            Text(
                text = annotatedString,
                fontSize = 11.sp,
                color = gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
