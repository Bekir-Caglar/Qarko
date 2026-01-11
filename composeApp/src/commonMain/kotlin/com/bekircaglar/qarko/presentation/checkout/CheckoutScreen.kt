package com.bekircaglar.qarko.presentation.checkout

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.bekircaglar.qarko.data.manager.CartManager
import com.bekircaglar.qarko.presentation.common.components.BackButton
import com.bekircaglar.qarko.presentation.common.components.QText
import com.bekircaglar.qarko.presentation.common.theme.black
import com.bekircaglar.qarko.presentation.common.theme.gray
import com.bekircaglar.qarko.presentation.common.theme.lightGray
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
fun CheckoutScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val deliveryFee = 15.0
    val cartTotal = CartManager.totalPrice
    val totalAmount = cartTotal + deliveryFee

    var orderNote by remember { mutableStateOf("") }
    val maxNoteLength = 200
    var selectedPaymentMethod by remember { mutableStateOf(0) } // 0: Card, 1: Cash

    // SavedCard: (cardName, maskedNumber - ilk 5 ve son 2 numara)
    data class SavedCard(
        val name: String,
        val maskedNumber: String
    )

    val savedCards = listOf(
        SavedCard("Ziraat Kartım", "5425 88** **** **18"),
        SavedCard("Garanti Bonus", "5234 56** **** **42")
    )
    var selectedCardIndex by remember { mutableStateOf(0) }
    var isCardListExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = surfaceGray,
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
            CheckoutBottomBar(totalAmount = totalAmount) {
                // Place order action
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
                    if (orderNote.isEmpty()) {
                        Text(
                            text = "Siparişiniz ile ilgili eklemek istediğiniz bir not var mı?",
                            color = gray,
                            fontSize = 14.sp
                        )
                    }
                    BasicTextField(
                        value = orderNote,
                        onValueChange = { if (it.length <= maxNoteLength) orderNote = it },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 14.sp,
                            color = black
                        ),
                        modifier = Modifier.fillMaxSize()
                    )

                    Text(
                        text = "${orderNote.length}/$maxNoteLength",
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
                        Column(
                        ) {
                            Text(
                                text = "Kampanya Seç",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = black
                            )
                            Text(
                                text = "Mevcut kampanyalarınızı kullanın",
                                fontSize = 12.sp,
                                color = gray,
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
                            text = "Seç",
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
                    // Payment Method TabRow
                    TabRow(
                        selectedTabIndex = selectedPaymentMethod,
                        containerColor = white,
                        contentColor = primary,
                        indicator = { tabPositions ->
                            Box(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[selectedPaymentMethod])
                                    .height(3.dp)
                                    .padding(horizontal = 32.dp)
                                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                    .background(primary)
                            )
                        },
                        divider = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(lightGray.copy(alpha = 0.5f))
                            )
                        }
                    ) {
                        paymentTabs.forEachIndexed { index, tab ->
                            val isSelected = selectedPaymentMethod == index
                            val textColor by animateColorAsState(
                                targetValue = if (isSelected) primary else gray,
                                animationSpec = tween(300),
                                label = "tabTextColor"
                            )

                            Tab(
                                selected = isSelected,
                                onClick = {
                                    selectedPaymentMethod = index
                                    if (index == 0) isCardListExpanded = true else isCardListExpanded = false
                                },
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
                                        tint = textColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.size(8.dp))
                                    QText(
                                        text = tab.title,
                                        color = textColor,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Payment Method Content
                    when (selectedPaymentMethod) {
                        0 -> {
                            // Card Payment Content
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(lighterGray.copy(alpha = 0.2f))
                            ) {
                                // Card Header
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
                                            contentDescription = "Mastercard",
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(modifier = Modifier.size(12.dp))
                                        Column {
                                            Text(
                                                text = savedCards[selectedCardIndex].name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = black
                                            )
                                            Text(
                                                text = savedCards[selectedCardIndex].maskedNumber,
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

                                // Expanded List
                                if (isCardListExpanded) {
                                    Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
                                        HorizontalDivider(color = gray.copy(alpha = 0.1f))
                                        Spacer(modifier = Modifier.height(8.dp))

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
                                                        contentDescription = "Mastercard",
                                                        modifier = Modifier.size(32.dp)
                                                    )
                                                    Spacer(modifier = Modifier.size(12.dp))
                                                    Column {
                                                        Text(
                                                            text = card.name,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            color = black
                                                        )
                                                        Text(
                                                            text = card.maskedNumber,
                                                            fontSize = 12.sp,
                                                            color = gray
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // Add Card Button
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
                                                .clickable { /* TODO: Add Card Logic */ }
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
                        1 -> {
                            // Cash Payment Content
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
                                    text = "Siparişiniz hazırlandığında kasada nakit veya kredi kartı ile ödeme yapabilirsiniz. Ekstra ücret alınmaz.",
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
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryRow(title = "Ara Toplam", amount = cartTotal)
                    SummaryRow(title = "Teslimat Ücreti", amount = deliveryFee)

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

            Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom bar
        }
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = black
            )
        }
        content()
    }
}

@Composable
fun PaymentOptionItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    leadingIcon: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(lighterGray.copy(alpha = 0.2f))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            leadingIcon()
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = black
            )
        }

        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = primary,
                unselectedColor = gray
            )
        )
    }
}

@Composable
fun DropdownSelector(
    text: String,
    leadingIcon: (@Composable () -> Unit)? = null,
    backgroundColor: Color = lighterGray.copy(alpha = 0.2f)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.size(12.dp))
            }
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = black
            )
        }
        Icon(
            imageVector = FeatherIcons.ChevronDown,
            contentDescription = null,
            tint = gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SummaryRow(title: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "₺${amount.toInt()}",
            fontSize = 14.sp,
            color = black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CheckoutBottomBar(
    totalAmount: Double,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary
                )
            ) {
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
                        text = "Siparişi Ver - ₺ ${totalAmount.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = white
                    )
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

            Spacer(modifier = Modifier.height(16.dp)) // Safe area padding
        }
    }
}
